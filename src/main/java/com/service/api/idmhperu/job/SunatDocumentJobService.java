package com.service.api.idmhperu.job;

import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.external.FacturacionResponse;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.SaleItemRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SunatDocumentJobService {

  private final DocumentRepository documentRepository;
  private final SaleItemRepository saleItemRepository;

  private final RestTemplate restTemplate = new RestTemplate();

  private static final String SUNAT_URL =
      "https://e-fact.facturacion-idmhperu.website/facturador-magistrack/facturador/sunat/comprobante/sent";

  @Scheduled(fixedRate = 1800000) // 30 minutes
  public void sendPendingDocuments() {

    log.info("Iniciando job de documentos pendientes de SUNAT");

    List<Document> pendingDocs = new ArrayList<>();
    pendingDocs.addAll(documentRepository.findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull("PENDIENTE", "01"));
    pendingDocs.addAll(documentRepository.findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull("PENDIENTE", "03"));

    log.info("Se encontraron {} documentos pendientes", pendingDocs.size());

    for (Document doc : pendingDocs) {

      try {

        Sale sale = doc.getSale();
        List<SaleItem> items = saleItemRepository.findBySaleIdAndDeletedAtIsNull(sale.getId());

        Map<String, Object> requestJson = buildInvoiceJson(doc, sale, items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
            new HttpEntity<>(requestJson, headers);

        ResponseEntity<FacturacionResponse> response =
            restTemplate.exchange(
                SUNAT_URL,
                HttpMethod.POST,
                entity,
                FacturacionResponse.class
            );

        processResponse(doc, response);

      } catch (Exception e) {

        doc.setStatus("ERROR");
        doc.setSunatMessage("Error al enviar: " + e.getMessage());
        log.error("Error al enviar el ID del documento {}", doc.getId(), e);
      }

      doc.setUpdatedBy("job-system");
      documentRepository.save(doc);
    }

    log.info("job de envío de documentos a SUNAT finalizado");
  }

  private void processResponse(Document doc,
                               ResponseEntity<FacturacionResponse> response) {

    FacturacionResponse body = response.getBody();

    if (response.getStatusCode().is2xxSuccessful()
        && body != null
        && body.isSuccess()) {

      FacturacionResponse.FacturacionData data = body.getData();

      if ("ACEPTADO".equalsIgnoreCase(data.getDeclarationResultType())) {
        doc.setStatus("ACEPTADO");
      } else {
        doc.setStatus("RECHAZADO");
      }

      doc.setSunatResponseCode(data.getResponseCode());
      doc.setSunatMessage(data.getMessage());
      doc.setHashCode(data.getHashCode());
      doc.setQrCode(data.getQrCode());
      doc.setXmlBase64(data.getXmlBase64());
      doc.setCdrBase64(data.getCdrBase64());

    } else {

      doc.setStatus("ERROR");
      doc.setSunatMessage("HTTP Error: " + response.getStatusCode());
    }
  }

  private Map<String, Object> buildInvoiceJson(Document doc,
                                               Sale sale,
                                               List<SaleItem> items) {

    Map<String, Object> root = new LinkedHashMap<>();

    Map<String, Object> comprobante = new LinkedHashMap<>();

    comprobante.put("tipoDocumento",
        doc.getDocumentTypeSunat().getCode().equals("01")
            ? "FACTURA"
            : "BOLETA");

    comprobante.put("compSerie", doc.getSeries());
    comprobante.put("compNumero", Integer.parseInt(doc.getSequence()));
    comprobante.put("moneda", sale.getCurrencyCode());
    comprobante.put("compFechaEmision",
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .format(LocalDate.now()));

    comprobante.put("compTotal",
        sale.getTotalAmount().setScale(2, RoundingMode.HALF_UP));

    // Cliente
    Map<String, Object> client = new LinkedHashMap<>();

    if (sale.getClient() != null) {
      client.put("clieNumeroDocumento",
          sale.getClient().getDocumentNumber());

      client.put("clieRazonSocial",
          sale.getClient().getBusinessName() != null
              ? sale.getClient().getBusinessName()
              : sale.getClient().getFirstName() + " "
              + sale.getClient().getLastName());

      client.put("tipoDocumentoIdentidad",
          sale.getClient().getDocumentType().getName());
    }

    comprobante.put("cliente", client);

    // Items
    List<Map<String, Object>> jsonItems = new ArrayList<>();

    for (SaleItem item : items) {

      BigDecimal quantity = item.getQuantity();
      BigDecimal unitPrice = item.getUnitPrice();
      BigDecimal valueUnit =
          unitPrice.divide(new BigDecimal("1.18"), 6, RoundingMode.HALF_UP);

      BigDecimal subtotal =
          valueUnit.multiply(quantity);

      BigDecimal igv =
          subtotal.multiply(new BigDecimal("0.18"));

      BigDecimal total =
          subtotal.add(igv);

      Map<String, Object> jsonItem = new LinkedHashMap<>();

      jsonItem.put("itcoUnidadMedida", "NIU");
      jsonItem.put("itcoDescripcion", item.getDescription());
      jsonItem.put("itcoCantidad", quantity);
      jsonItem.put("itcoValorUnitario",
          valueUnit.setScale(2, RoundingMode.HALF_UP));
      jsonItem.put("itcoPrecioUnitario",
          unitPrice.setScale(2, RoundingMode.HALF_UP));
      jsonItem.put("itcoSubTotal",
          subtotal.setScale(2, RoundingMode.HALF_UP));
      jsonItem.put("itcoIgv",
          igv.setScale(2, RoundingMode.HALF_UP));
      jsonItem.put("itcoTotal",
          total.setScale(2, RoundingMode.HALF_UP));
      jsonItem.put("tipoAfectacionIgv", "GRAVADO");

      jsonItems.add(jsonItem);
    }

    comprobante.put("lsItemComprobante", jsonItems);

    root.put("comprobante", comprobante);

    return root;
  }
}
