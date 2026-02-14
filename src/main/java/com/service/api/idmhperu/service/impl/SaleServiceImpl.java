package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.entity.PaymentMethod;
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
import com.service.api.idmhperu.service.SaleService;
import com.service.api.idmhperu.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

  @Override
  @Transactional
  public ApiResponse<SaleResponse> create(SaleRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Sale sale = new Sale();
    sale.setCurrencyCode("PEN");
    sale.setTaxPercentage(new BigDecimal("18"));
    sale.setCreatedBy(username);
    sale.setSaleDate(LocalDateTime.now());

    Client client = clientRepository.findById(request.getClientId())
        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    sale.setClient(client);

    // BORRADOR
    if (Boolean.TRUE.equals(request.getDraft())) {
      sale.setSaleStatus("BORRADOR");
    } else {
      sale.setSaleStatus("FINALIZADA");
    }

    sale = saleRepository.save(sale);

    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal total = BigDecimal.ZERO;

    // =========================
    // ITEMS
    // =========================
    for (SaleItemRequest itemReq : request.getItems()) {

      BigDecimal itemSubtotal =
          itemReq.getQuantity().multiply(itemReq.getUnitPrice());

      BigDecimal itemTax =
          itemSubtotal.multiply(new BigDecimal("0.18"));

      BigDecimal itemTotal = itemSubtotal.add(itemTax);

      SaleItem item = new SaleItem();
      item.setSale(sale);
      item.setItemType(itemReq.getItemType());
      item.setDescription(itemReq.getDescription());
      item.setQuantity(itemReq.getQuantity());
      item.setUnitPrice(itemReq.getUnitPrice());
      item.setSubtotalAmount(itemSubtotal);
      item.setTaxAmount(itemTax);
      item.setTotalAmount(itemTotal);
      item.setCreatedBy(username);

      saleItemRepository.save(item);

      subtotal = subtotal.add(itemSubtotal);
      tax = tax.add(itemTax);
      total = total.add(itemTotal);
    }

    sale.setSubtotalAmount(subtotal);
    sale.setTaxAmount(tax);
    sale.setTotalAmount(total);

    saleRepository.save(sale);

    // =========================
    // SI ES BORRADOR TERMINA AQUÍ
    // =========================
    if (Boolean.TRUE.equals(request.getDraft())) {
      return new ApiResponse<>("Venta guardada como borrador",
          mapper.toResponse(sale));
    }

    // =========================
    // VALIDACIÓN DE PAGOS
    // =========================
    if (request.getPayments() == null || request.getPayments().isEmpty()) {
      throw new BusinessValidationException("Debe registrar al menos un pago");
    }

    BigDecimal totalPaid = BigDecimal.ZERO;
    SalePayment lastCashPayment = null;

    for (SalePaymentRequest paymentReq : request.getPayments()) {

      PaymentMethod method = paymentMethodRepository
          .findById(paymentReq.getPaymentMethodId())
          .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado"));

      if (method.getRequiresProof() &&
          (paymentReq.getProofFileUrl() == null || paymentReq.getProofFileUrl().isBlank())) {
        throw new BusinessValidationException(
            "El método " + method.getName() + " requiere comprobante");
      }

      SalePayment payment = new SalePayment();
      payment.setSale(sale);
      payment.setPaymentMethod(method);
      payment.setAmountPaid(paymentReq.getAmountPaid());
      payment.setPaymentReference(paymentReq.getPaymentReference());
      payment.setProofFileUrl(paymentReq.getProofFileUrl());
      payment.setPaymentDate(LocalDateTime.now());
      payment.setChangeAmount(BigDecimal.ZERO);
      payment.setCreatedBy(username);

      payment = salePaymentRepository.save(payment);

      totalPaid = totalPaid.add(paymentReq.getAmountPaid());

      if ("CASH".equals(method.getCode())) {
        lastCashPayment = payment;
      }
    }

    // =========================
    // VALIDAR QUE CUBRA EL TOTAL
    // =========================
    if (totalPaid.compareTo(total) < 0) {
      throw new BusinessValidationException(
          "El total pagado no cubre el total de la venta");
    }

    // =========================
    // MANEJO DE VUELTO
    // =========================
    if (totalPaid.compareTo(total) > 0) {

      BigDecimal change = totalPaid.subtract(total);

      if (lastCashPayment == null) {
        throw new BusinessValidationException(
            "Si hay vuelto, debe existir un pago en efectivo (CASH)");
      }

      lastCashPayment.setChangeAmount(change);
      lastCashPayment.setUpdatedBy(username);
      salePaymentRepository.save(lastCashPayment);
    }

    // =========================
    // GENERAR DOCUMENTO SIEMPRE (NO BORRADOR)
    // =========================
    DocumentSeries series = documentSeriesRepository
        .findByIdForUpdate(request.getDocumentSeriesId())
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

    return new ApiResponse<>("Venta registrada correctamente",
        mapper.toResponse(sale));
  }

  @Override
  public ApiResponse<List<SaleResponse>> findAll(SaleFilter filter) {
    return new ApiResponse<>(
        "Ventas listadas correctamente",
        mapper.toResponseList(
            saleRepository.findAll(SaleSpecification.byFilter(filter))
        )
    );
  }
}
