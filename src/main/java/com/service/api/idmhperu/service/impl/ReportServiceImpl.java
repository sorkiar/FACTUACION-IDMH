package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteItem;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.response.SalesReportResponse;
import com.service.api.idmhperu.dto.response.SalesReportRowResponse;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.repository.SaleRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import com.service.api.idmhperu.service.ReportService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final SaleRepository saleRepository;
  private final CreditDebitNoteRepository creditDebitNoteRepository;
  private final ConfigurationService configurationService;

  @Override
  public SalesReportResponse salesReport(
      LocalDate startDate,
      LocalDate endDate,
      String clientIds,
      String productIds,
      String documentTypeCodes
  ) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(23, 59, 59);

    List<Sale> sales = saleRepository.findForReport(start, end);

    Set<Long> clientIdSet = parseIds(clientIds);
    if (!clientIdSet.isEmpty()) {
      sales = sales.stream()
          .filter(s -> clientIdSet.contains(s.getClient().getId()))
          .collect(Collectors.toList());
    }

    Set<Long> productIdSet = parseIds(productIds);
    if (!productIdSet.isEmpty()) {
      sales = sales.stream()
          .filter(s -> s.getItems().stream()
              .anyMatch(item -> item.getProduct() != null &&
                  productIdSet.contains(item.getProduct().getId())))
          .collect(Collectors.toList());
    }

    Set<String> docTypeCodeSet = parseStringSet(documentTypeCodes);

    // Filter sales by document type if specified (only for sale doc types: 01, 03)
    List<Sale> filteredSales = sales;
    if (!docTypeCodeSet.isEmpty()) {
      filteredSales = sales.stream()
          .filter(s -> s.getDocuments().stream()
              .anyMatch(doc -> doc.getDocumentTypeSunat() != null &&
                  docTypeCodeSet.contains(doc.getDocumentTypeSunat().getCode())))
          .collect(Collectors.toList());
    }

    Map<String, String> config = configurationService.getGroup("empresa_emisora");
    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    List<SalesReportRowResponse> rows = new ArrayList<>();

    // --- Rows from Sales (doc types 01, 03) ---
    for (Sale sale : filteredSales) {

      Document doc = sale.getDocuments().stream()
          .min(Comparator.comparing(Document::getIssueDate))
          .orElse(null);

      String document = "-";
      if (doc != null) {
        document = doc.getSeries() + "-" + doc.getSequence();
      }

      String documentTypeCode = (doc != null && doc.getDocumentTypeSunat() != null)
          ? doc.getDocumentTypeSunat().getCode() : null;
      String documentTypeName = (doc != null && doc.getDocumentTypeSunat() != null)
          ? doc.getDocumentTypeSunat().getName() : null;
      String sunatStatus = doc != null ? doc.getStatus() : null;

      String issueDate = sale.getSaleDate().format(dateFmt);
      String client = buildClientField(sale);

      for (SaleItem item : sale.getItems()) {
        BigDecimal itemBase = item.getSubtotalAmount() != null
            ? item.getSubtotalAmount() : BigDecimal.ZERO;
        BigDecimal itemTax = item.getTaxAmount() != null
            ? item.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal itemTotal = item.getTotalAmount() != null
            ? item.getTotalAmount() : BigDecimal.ZERO;

        rows.add(SalesReportRowResponse.builder()
            .issueDate(issueDate)
            .document(document)
            .documentTypeCode(documentTypeCode)
            .documentTypeName(documentTypeName)
            .sunatStatus(sunatStatus)
            .client(client)
            .itemDescription(buildItemDescription(item))
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .discountPercentage(item.getDiscountPercentage() != null
                ? item.getDiscountPercentage() : BigDecimal.ZERO)
            .currencyCode(sale.getCurrencyCode())
            .exchangeRate(sale.getExchangeRate() != null ? sale.getExchangeRate() : java.math.BigDecimal.ONE)
            .itemBaseAmount(itemBase)
            .itemTaxAmount(itemTax)
            .itemTotalAmount(itemTotal)
            .build());
      }
    }

    // --- Rows from Credit/Debit Notes (doc types 07, 08) ---
    boolean includeNotes = docTypeCodeSet.isEmpty()
        || docTypeCodeSet.contains("07")
        || docTypeCodeSet.contains("08");

    if (includeNotes) {
      List<CreditDebitNote> notes = creditDebitNoteRepository.findForReport(start, end);

      // Apply client filter
      if (!clientIdSet.isEmpty()) {
        notes = notes.stream()
            .filter(n -> n.getSale() != null && clientIdSet.contains(n.getSale().getClient().getId()))
            .collect(Collectors.toList());
      }

      // Apply product filter
      if (!productIdSet.isEmpty()) {
        notes = notes.stream()
            .filter(n -> n.getItems().stream()
                .anyMatch(item -> item.getProduct() != null &&
                    productIdSet.contains(item.getProduct().getId())))
            .collect(Collectors.toList());
      }

      // Apply doc type filter for notes (07 = Nota de Débito, 08 = Nota de Crédito)
      if (!docTypeCodeSet.isEmpty()) {
        notes = notes.stream()
            .filter(n -> n.getDocumentTypeSunat() != null &&
                docTypeCodeSet.contains(n.getDocumentTypeSunat().getCode()))
            .collect(Collectors.toList());
      }

      for (CreditDebitNote note : notes) {
        String document = note.getSeries() + "-" + note.getSequence();
        String documentTypeCode = note.getDocumentTypeSunat() != null
            ? note.getDocumentTypeSunat().getCode() : null;
        String documentTypeName = note.getDocumentTypeSunat() != null
            ? note.getDocumentTypeSunat().getName() : null;
        String sunatStatus = note.getStatus();
        String issueDate = note.getIssueDate().format(dateFmt);
        String client = note.getSale() != null
            ? buildClientField(note.getSale()) : "-";

        for (CreditDebitNoteItem item : note.getItems()) {
          BigDecimal itemBase = item.getSubtotalAmount() != null
              ? item.getSubtotalAmount() : BigDecimal.ZERO;
          BigDecimal itemTax = item.getTaxAmount() != null
              ? item.getTaxAmount() : BigDecimal.ZERO;
          BigDecimal itemTotal = item.getTotalAmount() != null
              ? item.getTotalAmount() : BigDecimal.ZERO;

          rows.add(SalesReportRowResponse.builder()
              .issueDate(issueDate)
              .document(document)
              .documentTypeCode(documentTypeCode)
              .documentTypeName(documentTypeName)
              .sunatStatus(sunatStatus)
              .client(client)
              .itemDescription(buildNoteItemDescription(item))
              .quantity(item.getQuantity())
              .unitPrice(item.getUnitPrice())
              .discountPercentage(item.getDiscountPercentage() != null
                  ? item.getDiscountPercentage() : BigDecimal.ZERO)
              .currencyCode(note.getCurrencyCode())
              .exchangeRate(note.getExchangeRate() != null ? note.getExchangeRate() : java.math.BigDecimal.ONE)
              .itemBaseAmount(itemBase)
              .itemTaxAmount(itemTax)
              .itemTotalAmount(itemTotal)
              .build());
        }
      }
    }

    // Sort all rows by issueDate descending
    rows.sort(Comparator.comparing(SalesReportRowResponse::getIssueDate).reversed());

    return SalesReportResponse.builder()
        .companyName(config.get("emprRazonSocial"))
        .dateRange(startDate.format(dateFmt) + " - " + endDate.format(dateFmt))
        .totalItems(rows.size())
        .rows(rows)
        .build();
  }

  private String buildClientField(Sale sale) {
    var client = sale.getClient();
    String name;
    if (client.getBusinessName() != null && !client.getBusinessName().isBlank()) {
      name = client.getBusinessName();
    } else {
      String full = ((client.getFirstName() != null ? client.getFirstName() : "") + " " +
          (client.getLastName() != null ? client.getLastName() : "")).trim();
      name = full.isBlank() ? "" : full;
    }
    return client.getDocumentNumber() + " - " + name;
  }

  private String buildItemDescription(SaleItem item) {
    String sku;
    if (item.getProduct() != null) {
      sku = item.getProduct().getSku();
    } else if (item.getService() != null) {
      sku = item.getService().getSku();
    } else {
      sku = "SRV0000000";
    }
    return sku + " - " + item.getDescription();
  }

  private String buildNoteItemDescription(CreditDebitNoteItem item) {
    String sku;
    if (item.getProduct() != null) {
      sku = item.getProduct().getSku();
    } else if (item.getService() != null) {
      sku = item.getService().getSku();
    } else {
      sku = "SRV0000000";
    }
    return sku + " - " + item.getDescription();
  }

  private Set<Long> parseIds(String csv) {
    if (csv == null || csv.isBlank()) return Set.of();
    return Arrays.stream(csv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(Long::parseLong)
        .collect(Collectors.toSet());
  }

  private Set<String> parseStringSet(String csv) {
    if (csv == null || csv.isBlank()) return Set.of();
    return Arrays.stream(csv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toSet());
  }
}
