package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.entity.PaymentMethod;
import com.service.api.idmhperu.dto.entity.Product;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.entity.SalePayment;
import com.service.api.idmhperu.dto.filter.SaleFilter;
import com.service.api.idmhperu.dto.mapper.SaleMapper;
import com.service.api.idmhperu.dto.request.SaleItemRequest;
import com.service.api.idmhperu.dto.request.SalePaymentRequest;
import com.service.api.idmhperu.dto.request.SaleRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.SaleResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.ClientRepository;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.DocumentSeriesRepository;
import com.service.api.idmhperu.repository.PaymentMethodRepository;
import com.service.api.idmhperu.repository.ProductRepository;
import com.service.api.idmhperu.repository.SaleItemRepository;
import com.service.api.idmhperu.repository.SalePaymentRepository;
import com.service.api.idmhperu.repository.SaleRepository;
import com.service.api.idmhperu.repository.ServiceRepository;
import com.service.api.idmhperu.repository.spec.SaleSpecification;
import com.service.api.idmhperu.service.DocumentPdfService;
import com.service.api.idmhperu.service.GoogleDriveService;
import com.service.api.idmhperu.service.SaleService;
import com.service.api.idmhperu.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

  private final SaleRepository saleRepository;
  private final SaleItemRepository saleItemRepository;
  private final ClientRepository clientRepository;
  private final ProductRepository productRepository;
  private final ServiceRepository serviceRepository;
  private final PaymentMethodRepository paymentMethodRepository;
  private final DocumentSeriesRepository documentSeriesRepository;
  private final SalePaymentRepository salePaymentRepository;
  private final DocumentRepository documentRepository;
  private final SaleMapper mapper;
  private final DocumentPdfService documentPdfService;
  private final GoogleDriveService googleDriveService;

  private static final String PAYMENT_PROOF_FOLDER_ID = "1muzPel7B0zlx4WHEPCiIDfoqKryxHIbn";

  @Override
  public ApiResponse<List<SaleResponse>> findAll(SaleFilter filter) {
    return new ApiResponse<>(
        "Ventas listadas correctamente",
        mapper.toResponseList(
            saleRepository.findAll(SaleSpecification.byFilter(filter))
        )
    );
  }

  @Override
  @Transactional
  public ApiResponse<SaleResponse> create(
      SaleRequest request,
      MultiValueMap<String, MultipartFile> paymentProofs
  ) {

    String username = JwtUtils.extractUsernameFromContext();

    Sale sale = new Sale();
    sale.setCurrencyCode("PEN");
    sale.setTaxPercentage(new BigDecimal("18"));
    sale.setCreatedBy(username);
    sale.setSaleDate(LocalDateTime.now());

    Client client = clientRepository.findById(request.getClientId())
        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    sale.setClient(client);

    if (Boolean.TRUE.equals(request.getDraft())) {
      sale.setSaleStatus("BORRADOR");
    } else {
      sale.setSaleStatus("CANCELADO");
    }

    sale = saleRepository.save(sale);

    Totals totals = rebuildItemsAndTotals(sale, request.getItems(), username);

    sale.setSubtotalAmount(totals.subtotal());
    sale.setTaxAmount(totals.tax());
    sale.setTotalAmount(totals.total());
    sale = saleRepository.save(sale);

    //  Si es borrador → termina aquí
    if (Boolean.TRUE.equals(request.getDraft())) {
      return new ApiResponse<>("Venta guardada como borrador",
          mapper.toResponse(sale));
    }

    // Finalización real
    processPayments(sale, request, paymentProofs, username);
    generateDocument(sale, request.getDocumentSeriesId(), username);

    Sale saleWithRelations = saleRepository
        .findByIdAndDeletedAtIsNull(sale.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

    return new ApiResponse<>("Venta registrada correctamente",
        mapper.toResponse(saleWithRelations));
  }

  @Override
  @Transactional
  public ApiResponse<SaleResponse> updateDraft(
      Long id,
      SaleRequest request,
      MultiValueMap<String, MultipartFile> paymentProofs
  ) {

    String username = JwtUtils.extractUsernameFromContext();

    Sale sale = saleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

    if (!"BORRADOR".equals(sale.getSaleStatus())) {
      throw new BusinessValidationException(
          "Solo se pueden actualizar ventas en estado BORRADOR");
    }

    if (request.getClientId() != null) {
      Client client = clientRepository.findById(request.getClientId())
          .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
      sale.setClient(client);
    }

    saleItemRepository.deleteBySaleId(sale.getId());

    Totals totals = rebuildItemsAndTotals(sale, request.getItems(), username);

    sale.setSubtotalAmount(totals.subtotal());
    sale.setTaxAmount(totals.tax());
    sale.setTotalAmount(totals.total());
    sale.setUpdatedBy(username);
    sale = saleRepository.save(sale);

    //  Finalizar borrador
    processPayments(sale, request, paymentProofs, username);
    generateDocument(sale, request.getDocumentSeriesId(), username);

    sale.setSaleStatus("CANCELADO");
    saleRepository.save(sale);

    Sale saleWithRelations = saleRepository
        .findByIdAndDeletedAtIsNull(sale.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

    return new ApiResponse<>("Venta finalizada correctamente",
        mapper.toResponse(saleWithRelations));
  }

  // ============================================================
  // Helpers de lógica de negocio
  // ============================================================

  private Totals rebuildItemsAndTotals(Sale sale, List<SaleItemRequest> items, String username) {

    if (items == null || items.isEmpty()) {
      throw new BusinessValidationException("Debe registrar al menos un item");
    }

    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal total = BigDecimal.ZERO;

    BigDecimal taxRate = new BigDecimal("0.18");
    BigDecimal divisor = BigDecimal.ONE.add(taxRate);

    for (SaleItemRequest itemReq : items) {

      BigDecimal itemTotal =
          itemReq.getQuantity()
              .multiply(itemReq.getUnitPrice())
              .setScale(2, RoundingMode.HALF_UP);

      BigDecimal itemBase =
          itemTotal.divide(divisor, 10, RoundingMode.HALF_UP);

      BigDecimal itemTax =
          itemTotal.subtract(itemBase);

      itemBase = itemBase.setScale(2, RoundingMode.HALF_UP);
      itemTax = itemTax.setScale(2, RoundingMode.HALF_UP);

      SaleItem item = new SaleItem();

      if ("PRODUCTO".equals(itemReq.getItemType())) {
        if (itemReq.getProductId() == null) {
          throw new BusinessValidationException("productId es obligatorio cuando itemType=PRODUCTO");
        }
        Product product = productRepository.findById(itemReq.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        item.setProduct(product);
      }

      if ("SERVICIO".equals(itemReq.getItemType())) {
        if (itemReq.getServiceId() == null) {
          throw new BusinessValidationException("serviceId es obligatorio cuando itemType=SERVICIO");
        }
        com.service.api.idmhperu.dto.entity.Service service = serviceRepository.findById(itemReq.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        item.setService(service);
      }

      item.setSale(sale);
      item.setItemType(itemReq.getItemType());
      item.setDescription(itemReq.getDescription());
      item.setQuantity(itemReq.getQuantity());
      item.setUnitPrice(itemReq.getUnitPrice());
      item.setSubtotalAmount(itemBase);
      item.setTaxAmount(itemTax);
      item.setTotalAmount(itemTotal);
      item.setCreatedBy(username);

      saleItemRepository.save(item);

      subtotal = subtotal.add(itemBase);
      tax = tax.add(itemTax);
      total = total.add(itemTotal);
    }

    return new Totals(subtotal, tax, total);
  }

  private void processPayments(
      Sale sale,
      SaleRequest request,
      MultiValueMap<String, MultipartFile> paymentProofs,
      String username
  ) {

    if (request.getPayments() == null || request.getPayments().isEmpty()) {
      throw new BusinessValidationException("Debe registrar al menos un pago");
    }

    BigDecimal total = sale.getTotalAmount();
    BigDecimal totalPaid = BigDecimal.ZERO;
    SalePayment lastCashPayment = null;

    salePaymentRepository.deleteBySaleId(sale.getId());

    for (SalePaymentRequest paymentReq : request.getPayments()) {

      PaymentMethod method = paymentMethodRepository
          .findById(paymentReq.getPaymentMethodId())
          .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado"));

      MultipartFile proofFile = null;

      if (paymentReq.getProofKey() != null
          && paymentProofs != null
          && paymentProofs.containsKey(paymentReq.getProofKey())) {

        proofFile = paymentProofs.getFirst(paymentReq.getProofKey());
      }

      // CASH nunca permite archivo
      if ("CASH".equals(method.getCode())
          && proofFile != null && !proofFile.isEmpty()) {

        throw new BusinessValidationException(
            "El método CASH no debe tener comprobante");
      }

      // Si requiere comprobante
      if (method.getRequiresProof()
          && (proofFile == null || proofFile.isEmpty())) {

        throw new BusinessValidationException(
            "El método " + method.getName() + " requiere comprobante");
      }

      String proofUrl = null;

      if (proofFile != null && !proofFile.isEmpty()) {
        proofUrl = uploadPaymentProofToDrive(proofFile, username);
      }

      SalePayment payment = new SalePayment();
      payment.setSale(sale);
      payment.setPaymentMethod(method);
      payment.setAmountPaid(paymentReq.getAmountPaid());
      payment.setPaymentReference(paymentReq.getPaymentReference());
      payment.setProofFileUrl(proofUrl);
      payment.setPaymentDate(LocalDateTime.now());
      payment.setChangeAmount(BigDecimal.ZERO);
      payment.setCreatedBy(username);

      payment = salePaymentRepository.save(payment);

      totalPaid = totalPaid.add(paymentReq.getAmountPaid());

      if ("CASH".equals(method.getCode())) {
        lastCashPayment = payment;
      }
    }

    if (totalPaid.compareTo(total) < 0) {
      throw new BusinessValidationException(
          "El total pagado no cubre el total de la venta");
    }

    if (totalPaid.compareTo(total) > 0) {

      BigDecimal change = totalPaid.subtract(total)
          .setScale(2, RoundingMode.HALF_UP);

      if (lastCashPayment == null) {
        throw new BusinessValidationException(
            "Si hay vuelto, debe existir un pago en efectivo (CASH)");
      }

      lastCashPayment.setChangeAmount(change);
      lastCashPayment.setUpdatedBy(username);
      salePaymentRepository.save(lastCashPayment);
    }
  }

  private void generateDocument(
      Sale sale,
      Long documentSeriesId,
      String username
  ) {

    if (documentSeriesId == null) {
      throw new BusinessValidationException(
          "documentSeriesId es obligatorio al finalizar la venta");
    }

    DocumentSeries series = documentSeriesRepository
        .findByIdForUpdate(documentSeriesId)
        .orElseThrow(() -> new ResourceNotFoundException("Serie no encontrada"));

    Integer nextSequence = series.getCurrentSequence() + 1;
    series.setCurrentSequence(nextSequence);
    documentSeriesRepository.save(series);

    String formattedSequence = String.format("%08d", nextSequence);

    Document document = new Document();
    document.setSale(sale);
    document.setDocumentSeries(series);
    document.setDocumentTypeSunat(series.getDocumentTypeSunat());
    document.setSeries(series.getSeries());
    document.setSequence(formattedSequence);
    document.setIssueDate(LocalDateTime.now());
    document.setStatus("PENDIENTE");
    document.setCreatedBy(username);

    documentRepository.save(document);

    documentPdfService.generatePdf(sale.getId());
  }

  private String uploadPaymentProofToDrive(
      MultipartFile file,
      String username
  ) {
    try {
      String originalName = file.getOriginalFilename();
      String extension = "";

      if (originalName != null && originalName.contains(".")) {
        extension = originalName.substring(originalName.lastIndexOf("."));
      }

      String safePrefix =
          "sale_payment_" + System.currentTimeMillis();

      File temp = convertMultipartToFile(file, safePrefix + extension);

      return googleDriveService.uploadFileWithPublicAccess(
          temp,
          PAYMENT_PROOF_FOLDER_ID
      );

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al subir comprobante de pago: " + e.getMessage()
      );
    }
  }

  private File convertMultipartToFile(MultipartFile multipart, String prefix) throws IOException {
    File file = File.createTempFile(prefix, "");
    multipart.transferTo(file);
    return file;
  }

  // Helper interno simple para retornos de totales
  private record Totals(BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
  }
}
