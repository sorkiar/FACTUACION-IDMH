package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteItem;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteType;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.entity.DocumentTypeSunat;
import com.service.api.idmhperu.dto.entity.Product;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.filter.CreditDebitNoteFilter;
import com.service.api.idmhperu.dto.mapper.CreditDebitNoteMapper;
import com.service.api.idmhperu.dto.request.CreditDebitNoteItemRequest;
import com.service.api.idmhperu.dto.request.CreditDebitNoteRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CreditDebitNoteResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.CreditDebitNoteItemRepository;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.repository.CreditDebitNoteTypeRepository;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.DocumentSeriesRepository;
import com.service.api.idmhperu.repository.DocumentTypeSunatRepository;
import com.service.api.idmhperu.repository.ProductRepository;
import com.service.api.idmhperu.repository.SaleRepository;
import com.service.api.idmhperu.repository.ServiceRepository;
import com.service.api.idmhperu.repository.spec.CreditDebitNoteSpecification;
import com.service.api.idmhperu.service.CreditDebitNotePdfService;
import com.service.api.idmhperu.service.CreditDebitNoteService;
import com.service.api.idmhperu.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditDebitNoteServiceImpl implements CreditDebitNoteService {

  private final CreditDebitNoteRepository noteRepository;
  private final CreditDebitNoteItemRepository noteItemRepository;
  private final CreditDebitNoteTypeRepository noteTypeRepository;
  private final SaleRepository saleRepository;
  private final DocumentRepository documentRepository;
  private final DocumentSeriesRepository documentSeriesRepository;
  private final DocumentTypeSunatRepository documentTypeSunatRepository;
  private final ProductRepository productRepository;
  private final ServiceRepository serviceRepository;
  private final CreditDebitNoteMapper mapper;
  private final CreditDebitNotePdfService creditDebitNotePdfService;

  @Override
  public ApiResponse<List<CreditDebitNoteResponse>> findAll(CreditDebitNoteFilter filter) {
    List<CreditDebitNote> notes = noteRepository.findAll(
        CreditDebitNoteSpecification.byFilter(filter));
    return new ApiResponse<>("Notas listadas correctamente", mapper.toResponseList(notes));
  }

  @Override
  public ApiResponse<CreditDebitNoteResponse> findById(Long id) {
    CreditDebitNote note = noteRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada"));
    return new ApiResponse<>("Nota obtenida correctamente", mapper.toResponse(note));
  }

  @Override
  @Transactional
  public ApiResponse<CreditDebitNoteResponse> create(CreditDebitNoteRequest request) {

    String username = JwtUtils.extractUsernameFromContext();

    // 1. Buscar el tipo de nota (C01-C13 / D01-D12)
    CreditDebitNoteType noteType = noteTypeRepository
        .findByCodeAndStatusNot(request.getNoteTypeCode().toUpperCase(), 0)
        .orElseThrow(() -> new BusinessValidationException(
            "Tipo de nota inválido: " + request.getNoteTypeCode()
                + ". Use C01-C13 para crédito o D01-D12 para débito."));

    // 2. Determinar tipo de documento SUNAT (07 = crédito, 08 = débito)
    String documentTypeCode = "CREDITO".equals(noteType.getNoteCategory()) ? "07" : "08";

    // 3. Obtener la venta original
    Sale sale = saleRepository.findByIdAndDeletedAtIsNull(request.getSaleId())
        .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

    // 4. Obtener y validar el documento original (factura o boleta)
    Document originalDocument = documentRepository.findById(request.getOriginalDocumentId())
        .orElseThrow(() -> new ResourceNotFoundException("Documento original no encontrado"));

    if (!originalDocument.getSale().getId().equals(sale.getId())) {
      throw new BusinessValidationException(
          "El documento original no pertenece a la venta indicada");
    }

    if (!"ACEPTADO".equals(originalDocument.getStatus())) {
      throw new BusinessValidationException(
          "Solo se puede emitir notas sobre documentos aceptados por SUNAT. "
              + "Estado actual: " + originalDocument.getStatus());
    }

    String origDocTypeCode = originalDocument.getDocumentTypeSunat().getCode();
    if (!"01".equals(origDocTypeCode) && !"03".equals(origDocTypeCode)) {
      throw new BusinessValidationException(
          "El documento original debe ser una Factura (01) o Boleta (03)");
    }

    // 5. Obtener el tipo de documento SUNAT para la nota
    DocumentTypeSunat documentTypeSunat = documentTypeSunatRepository
        .findById(documentTypeCode)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Tipo de documento SUNAT no encontrado: " + documentTypeCode));

    // 6. Reservar secuencia en la serie
    DocumentSeries series = documentSeriesRepository
        .findByIdForUpdate(request.getDocumentSeriesId())
        .orElseThrow(() -> new ResourceNotFoundException("Serie no encontrada"));

    if (!documentTypeCode.equals(series.getDocumentTypeSunat().getCode())) {
      throw new BusinessValidationException(
          "La serie seleccionada no corresponde al tipo de nota ("
              + ("07".equals(documentTypeCode) ? "Nota de Crédito" : "Nota de Débito") + ")");
    }

    Integer nextSequence = series.getCurrentSequence() + 1;
    series.setCurrentSequence(nextSequence);
    documentSeriesRepository.save(series);

    // 7. Crear la nota
    CreditDebitNote note = new CreditDebitNote();
    note.setSale(sale);
    note.setOriginalDocument(originalDocument);
    note.setDocumentTypeSunat(documentTypeSunat);
    note.setDocumentSeries(series);
    note.setSeries(series.getSeries());
    note.setSequence(String.format("%08d", nextSequence));
    note.setIssueDate(LocalDateTime.now());
    note.setCreditDebitNoteType(noteType);
    note.setReason(request.getReason());
    note.setCurrencyCode(sale.getCurrencyCode());
    note.setTaxPercentage(sale.getTaxPercentage());
    note.setStatus("PENDIENTE");
    note.setCreatedBy(username);

    note = noteRepository.save(note);

    // 8. Procesar items y calcular totales
    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal total = BigDecimal.ZERO;

    BigDecimal divisor = BigDecimal.ONE.add(new BigDecimal("0.18"));

    for (CreditDebitNoteItemRequest itemReq : request.getItems()) {

      BigDecimal itemTotal = itemReq.getQuantity()
          .multiply(itemReq.getUnitPrice())
          .setScale(2, RoundingMode.HALF_UP);

      BigDecimal itemBase = itemTotal.divide(divisor, 10, RoundingMode.HALF_UP);
      BigDecimal itemTax = itemTotal.subtract(itemBase);

      itemBase = itemBase.setScale(2, RoundingMode.HALF_UP);
      itemTax = itemTax.setScale(2, RoundingMode.HALF_UP);

      CreditDebitNoteItem item = new CreditDebitNoteItem();
      item.setCreditDebitNote(note);
      item.setItemType(itemReq.getItemType());
      item.setDescription(itemReq.getDescription());
      item.setQuantity(itemReq.getQuantity());
      item.setUnitPrice(itemReq.getUnitPrice());
      item.setSubtotalAmount(itemBase);
      item.setTaxAmount(itemTax);
      item.setTotalAmount(itemTotal);
      item.setCreatedBy(username);

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
        com.service.api.idmhperu.dto.entity.Service service = serviceRepository
            .findById(itemReq.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        item.setService(service);
      }

      noteItemRepository.save(item);

      subtotal = subtotal.add(itemBase);
      tax = tax.add(itemTax);
      total = total.add(itemTotal);
    }

    note.setSubtotalAmount(subtotal);
    note.setTaxAmount(tax);
    note.setTotalAmount(total);
    noteRepository.save(note);

    creditDebitNotePdfService.generatePdf(note.getId());

    CreditDebitNote saved = noteRepository.findByIdAndDeletedAtIsNull(note.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada"));

    String tipoNota = "CREDITO".equals(noteType.getNoteCategory()) ? "crédito" : "débito";
    return new ApiResponse<>(
        "Nota de " + tipoNota + " registrada correctamente",
        mapper.toResponse(saved));
  }
}
