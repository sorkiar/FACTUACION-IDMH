package com.service.api.idmhperu.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteItem;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.external.*;
import com.service.api.idmhperu.repository.CreditDebitNoteItemRepository;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.SaleItemRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import com.service.api.idmhperu.service.GoogleDriveService;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SunatDocumentJobService {

  private final DocumentRepository documentRepository;
  private final SaleItemRepository saleItemRepository;
  private final CreditDebitNoteRepository creditDebitNoteRepository;
  private final CreditDebitNoteItemRepository creditDebitNoteItemRepository;
  private final ConfigurationService configurationService;
  private final GoogleDriveService googleDriveService;

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${sunat.url}")
  private String sunatUrl;

  private static final String DRIVE_FOLDER_ID = "1ysDbcKhd4ZikJ17k4330pzFWH2_Vycpz";
  private static final String COMPANY_RUC = "20602592457";

  @Scheduled(fixedRate = 1800000)
  public void sendPendingDocuments() {

    log.info("Iniciando proceso de envío a SUNAT...");

    List<Document> pendientes = new ArrayList<>();
    pendientes.addAll(
        documentRepository
            .findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull("PENDIENTE", "01"));
    pendientes.addAll(
        documentRepository
            .findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull("PENDIENTE", "03"));

    log.info("Documentos pendientes encontrados: {}", pendientes.size());

    for (Document doc : pendientes) {

      try {

        Sale sale = doc.getSale();
        List<SaleItem> items =
            saleItemRepository.findBySaleIdAndDeletedAtIsNull(sale.getId());

        SunatSendRequest request = buildRequest(doc, sale, items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SunatSendRequest> entity =
            new HttpEntity<>(request, headers);

        try {
          ObjectMapper mapper = new ObjectMapper();
          mapper.findAndRegisterModules();
          String json = mapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(request);

          log.info("====== JSON ENVIADO A SUNAT ======\n{}", json);

        } catch (Exception e) {
          log.error("Error serializando JSON", e);
        }

        ResponseEntity<FacturacionResponse> response =
            restTemplate.exchange(
                sunatUrl,
                HttpMethod.POST,
                entity,
                FacturacionResponse.class);

        processResponse(doc, response);

      } catch (Exception e) {

        doc.setStatus("ERROR");
        doc.setSunatMessage("Error enviando: " + e.getMessage());
        log.error("Error enviando documento {}", doc.getId(), e);
      }

      doc.setUpdatedBy("job-system");
      documentRepository.save(doc);
    }

    log.info("Proceso SUNAT finalizado.");

    sendPendingCreditDebitNotes();
  }

  // =====================================================
  // NOTAS DE CRÉDITO Y DÉBITO
  // =====================================================

  private void sendPendingCreditDebitNotes() {

    log.info("Iniciando proceso de notas de crédito/débito...");

    List<CreditDebitNote> pendientes = new ArrayList<>();
    pendientes.addAll(
        creditDebitNoteRepository
            .findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull("PENDIENTE", "07"));
    pendientes.addAll(
        creditDebitNoteRepository
            .findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull("PENDIENTE", "08"));

    log.info("Notas pendientes encontradas: {}", pendientes.size());

    for (CreditDebitNote note : pendientes) {

      try {

        List<CreditDebitNoteItem> items =
            creditDebitNoteItemRepository
                .findByCreditDebitNoteIdAndDeletedAtIsNull(note.getId());

        SunatSendRequest request = buildNoteRequest(note, items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SunatSendRequest> entity = new HttpEntity<>(request, headers);

        try {
          ObjectMapper mapper = new ObjectMapper();
          mapper.findAndRegisterModules();
          String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
          log.info("====== JSON NOTA ENVIADO A SUNAT ======\n{}", json);
        } catch (Exception e) {
          log.error("Error serializando JSON de nota", e);
        }

        ResponseEntity<FacturacionResponse> response =
            restTemplate.exchange(sunatUrl, HttpMethod.POST, entity, FacturacionResponse.class);

        processNoteResponse(note, response);

      } catch (Exception e) {
        note.setStatus("ERROR");
        note.setSunatMessage("Error enviando: " + e.getMessage());
        log.error("Error enviando nota {}", note.getId(), e);
      }

      note.setUpdatedBy("job-system");
      creditDebitNoteRepository.save(note);
    }

    log.info("Proceso notas SUNAT finalizado.");
  }

  private SunatSendRequest buildNoteRequest(
      CreditDebitNote note,
      List<CreditDebitNoteItem> items) {

    Map<String, String> config = configurationService.getGroup("empresa_emisora");

    // EMPRESA
    CompanySendRequest empresa = new CompanySendRequest();
    empresa.setEmprRuc(config.get("emprRuc"));
    empresa.setEmprRazonSocial(config.get("emprRazonSocial"));
    empresa.setEmprDireccionFiscal(config.get("emprDireccionFiscal"));
    empresa.setEmprCodigoEstablecimientoSunat(config.get("emprCodigoEstablecimientoSunat"));
    empresa.setEmprLeyAmazonia(Boolean.parseBoolean(config.get("emprLeyAmazonia")));
    empresa.setEmprProduccion(Boolean.parseBoolean(config.get("emprProduccion")));
    empresa.setEmprCertificadoLLavePublica(config.get("emprCertificadoLlavePublica"));
    empresa.setEmprCertificadoLLavePrivada(config.get("emprCertificadoLlavePrivada"));
    empresa.setEmprUsuarioSecundario(config.get("emprUsuarioSecundario"));
    empresa.setEmprClaveUsuarioSecundario(config.get("emprClaveUsuarioSecundario"));

    UbigeoSendRequest ubigeo = new UbigeoSendRequest();
    ubigeo.setUbigUbigeo(config.get("ubigUbigeo"));
    ubigeo.setUbigDepartamento(config.get("ubigDepartamento"));
    ubigeo.setUbigProvincia(config.get("ubigProvincia"));
    ubigeo.setUbigDistrito(config.get("ubigDistrito"));
    empresa.setUbigeo(ubigeo);

    // CLIENTE (del documento original)
    Sale sale = note.getSale();
    Document originalDoc = note.getOriginalDocument();
    String origDocTypeCode = originalDoc.getDocumentTypeSunat().getCode();

    ClientSendRequest cliente = new ClientSendRequest();
    cliente.setClieNumeroDocumento(sale.getClient().getDocumentNumber());
    cliente.setClieDireccion(sale.getClient().getAddress());

    if ("01".equals(origDocTypeCode)) {
      cliente.setClieRazonSocial(sale.getClient().getBusinessName());
      cliente.setTipoDocumentoIdentidad("RUC");
    } else {
      cliente.setClieRazonSocial(
          sale.getClient().getFirstName() + " " + sale.getClient().getLastName());
      cliente.setTipoDocumentoIdentidad("DNI");
    }

    // ITEMS
    List<ItemSendRequest> itemDtos = new ArrayList<>();
    for (CreditDebitNoteItem item : items) {

      BigDecimal cantidad = item.getQuantity();
      BigDecimal precioConIgv = item.getUnitPrice();
      BigDecimal valorUnitario = precioConIgv.divide(
          new BigDecimal("1.18"), 6, RoundingMode.HALF_UP);
      BigDecimal subtotal = valorUnitario.multiply(cantidad);
      BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"));
      BigDecimal total = subtotal.add(igv);

      ItemSendRequest dto = new ItemSendRequest();
      String unidad = "NIU";
      if (item.getUnitMeasure() != null && item.getUnitMeasure().getCodeSunat() != null) {
        unidad = item.getUnitMeasure().getCodeSunat();
      }
      dto.setItcoUnidadMedida(unidad);
      dto.setItcoDescripcion(item.getDescription());
      dto.setItcoCantidad(cantidad);
      dto.setItcoValorUnitario(valorUnitario.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoPrecioUnitario(precioConIgv.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoSubTotal(subtotal.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoIgv(igv.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoTotal(total.setScale(2, RoundingMode.HALF_UP));
      dto.setTipoAfectacionIgv("GRAVADO");
      itemDtos.add(dto);
    }

    // NOTA (referencia al documento original)
    String tipoNotaEnumName = resolveNoteTypeEnumName(note.getCreditDebitNoteType().getCode());
    String tipoDocAfectaEnumName = "01".equals(origDocTypeCode) ? "FACTURA" : "BOLETA";

    NotaSendRequest nota = new NotaSendRequest();
    nota.setNotaSerieModifica(originalDoc.getSeries());
    nota.setNotaNumeroModifica(Integer.parseInt(originalDoc.getSequence()));
    nota.setNotaFechaModifica(originalDoc.getIssueDate().toLocalDate().toString());
    nota.setTipoNotaCreditoDebito(tipoNotaEnumName);
    nota.setTipoDocumentoAfecta(tipoDocAfectaEnumName);

    // COMPROBANTE
    String tipoDocumento = "07".equals(note.getDocumentTypeSunat().getCode())
        ? "NOTA_CREDITO" : "NOTA_DEBITO";

    DocumentSendRequest comprobante = new DocumentSendRequest();
    comprobante.setCompSerie(note.getSeries());
    comprobante.setCompNumero(Integer.parseInt(note.getSequence()));
    comprobante.setCompFechaEmision(LocalDate.now().toString());
    comprobante.setCompPorcentajeIgv(new BigDecimal("18"));
    comprobante.setCompCondicionPago("CONTADO");
    comprobante.setCompMedioPago("EFECTIVO");
    comprobante.setMoneda(note.getCurrencyCode());
    comprobante.setTipoDocumento(tipoDocumento);
    comprobante.setTipoOperacion("VENTA_INTERNA");
    comprobante.setCliente(cliente);
    comprobante.setLsItemComprobante(itemDtos);
    comprobante.setNota(nota);

    SunatSendRequest request = new SunatSendRequest();
    request.setEmpresa(empresa);
    request.setComprobante(comprobante);

    return request;
  }

  private void processNoteResponse(CreditDebitNote note,
      ResponseEntity<FacturacionResponse> response) {

    FacturacionResponse body = response.getBody();

    if (response.getStatusCode().is2xxSuccessful() && body != null && body.isSuccess()) {

      var data = body.getData();

      if ("ACEPTADO".equalsIgnoreCase(data.getDeclarationResultType())) {
        note.setStatus("ACEPTADO");
      } else {
        note.setStatus("RECHAZADO");
      }

      note.setSunatResponseCode(data.getResponseCode());
      note.setSunatMessage(data.getMessage());
      note.setHashCode(data.getHashCode());
      note.setQrCode(data.getQrCode());
      note.setXmlBase64(data.getXmlBase64());
      note.setCdrBase64(data.getCdrBase64());

      if ("ACEPTADO".equals(note.getStatus())
          && data.getXmlBase64() != null
          && data.getCdrBase64() != null) {
        String[] urls = uploadXmlAndCdr(
            note.getDocumentTypeSunat().getCode(),
            note.getSeries(), note.getSequence(),
            data.getXmlBase64(), data.getCdrBase64());
        note.setXmlUrl(urls[0]);
        note.setCdrUrl(urls[1]);
      }

    } else {
      note.setStatus("ERROR");
      note.setSunatMessage("HTTP Error: " + response.getStatusCode());
    }
  }

  /**
   * Mapea el código de nota (C01, D02, etc.) al nombre del enum
   * Catalog09_10TipoNotaCreditoDebito del facturador.
   */
  private String resolveNoteTypeEnumName(String noteTypeCode) {
    return switch (noteTypeCode) {
      case "C01" -> "CREDITO_ANULACION_OPERACION";
      case "C02" -> "CREDITO_ANULACION_ERROR_RUC";
      case "C03" -> "CREDITO_CORRECCION_ERROR_DESCRIPCION";
      case "C04" -> "CREDITO_DESCUENTO_GLOBAL";
      case "C05" -> "CREDITO_DESCUENTO_POR_ITEM";
      case "C06" -> "CREDITO_DEVOLUCION_TOTAL";
      case "C07" -> "CREDITO_DEVOLUCION_POR_ITEM";
      case "C08" -> "CREDITO_BONIFICACION";
      case "C09" -> "CREDITO_DISMINUCION_VALOR";
      case "C10" -> "CREDITO_OTROS_CONCEPTOS";
      case "C11" -> "CREDITO_AJUSTES_OPERACIONES_EXPORTACION";
      case "C12" -> "CREDITO_AJUSTES_IVAP";
      case "C13" -> "CREDITO_AJUSTES_FECHA_MONTO_CREDITO";
      case "D01" -> "DEBITO_INTERESES_MORA";
      case "D02" -> "DEBITO_AUMENTO_VALOR";
      case "D03" -> "DEBITO_PENALIDADES";
      case "D11" -> "DEBITO_AJUSTES_OPERACIONES_EXPORTACION";
      case "D12" -> "DEBITO_AJUSTES_IVAP";
      default -> throw new IllegalArgumentException("Código de nota inválido: " + noteTypeCode);
    };
  }

  // =====================================================
  // BUILD REQUEST
  // =====================================================

  private SunatSendRequest buildRequest(
      Document doc,
      Sale sale,
      List<SaleItem> items) {

    Map<String, String> config =
        configurationService.getGroup("empresa_emisora");

    // ==========================
    // EMPRESA
    // ==========================
    CompanySendRequest empresa = new CompanySendRequest();
    empresa.setEmprRuc(config.get("emprRuc"));
    empresa.setEmprRazonSocial(config.get("emprRazonSocial"));
    empresa.setEmprDireccionFiscal(config.get("emprDireccionFiscal"));
    empresa.setEmprCodigoEstablecimientoSunat(
        config.get("emprCodigoEstablecimientoSunat"));
    empresa.setEmprLeyAmazonia(
        Boolean.parseBoolean(config.get("emprLeyAmazonia")));
    empresa.setEmprProduccion(
        Boolean.parseBoolean(config.get("emprProduccion")));
    empresa.setEmprCertificadoLLavePublica(
        config.get("emprCertificadoLlavePublica"));
    empresa.setEmprCertificadoLLavePrivada(
        config.get("emprCertificadoLlavePrivada"));
    empresa.setEmprUsuarioSecundario(
        config.get("emprUsuarioSecundario"));
    empresa.setEmprClaveUsuarioSecundario(
        config.get("emprClaveUsuarioSecundario"));

    UbigeoSendRequest ubigeo = new UbigeoSendRequest();
    ubigeo.setUbigUbigeo(config.get("ubigUbigeo"));
    ubigeo.setUbigDepartamento(config.get("ubigDepartamento"));
    ubigeo.setUbigProvincia(config.get("ubigProvincia"));
    ubigeo.setUbigDistrito(config.get("ubigDistrito"));

    empresa.setUbigeo(ubigeo);

    // ==========================
    // CLIENTE
    // ==========================
    ClientSendRequest cliente = new ClientSendRequest();

    cliente.setClieNumeroDocumento(
        sale.getClient().getDocumentNumber());

    cliente.setClieDireccion(
        sale.getClient().getAddress());

    if ("01".equals(doc.getDocumentTypeSunat().getCode())) {
      cliente.setClieRazonSocial(
          sale.getClient().getBusinessName());
      cliente.setTipoDocumentoIdentidad(
          "01".equals(doc.getDocumentTypeSunat().getCode())
              ? "RUC"
              : "DNI"
      );
    } else {
      cliente.setClieRazonSocial(
          sale.getClient().getFirstName() + " "
              + sale.getClient().getLastName());
      cliente.setTipoDocumentoIdentidad(
          "01".equals(doc.getDocumentTypeSunat().getCode())
              ? "RUC"
              : "DNI"
      );
    }

    // ==========================
    // ITEMS
    // ==========================
    List<ItemSendRequest> itemDtos = new ArrayList<>();

    for (SaleItem item : items) {

      BigDecimal cantidad = item.getQuantity();
      BigDecimal precioConIgv = item.getUnitPrice();

      BigDecimal valorUnitario =
          precioConIgv.divide(new BigDecimal("1.18"),
              6, RoundingMode.HALF_UP);

      BigDecimal subtotal =
          valorUnitario.multiply(cantidad);

      BigDecimal igv =
          subtotal.multiply(new BigDecimal("0.18"));

      BigDecimal total =
          subtotal.add(igv);

      ItemSendRequest dto = new ItemSendRequest();
      String unidad = "NIU";
      if (item.getUnitMeasure() != null &&
          item.getUnitMeasure().getCodeSunat() != null) {

        unidad = item.getUnitMeasure().getCodeSunat();
      }
      dto.setItcoUnidadMedida(unidad);
      dto.setItcoDescripcion(item.getDescription());
      dto.setItcoCantidad(cantidad);
      dto.setItcoValorUnitario(
          valorUnitario.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoPrecioUnitario(
          precioConIgv.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoSubTotal(
          subtotal.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoIgv(
          igv.setScale(2, RoundingMode.HALF_UP));
      dto.setItcoTotal(
          total.setScale(2, RoundingMode.HALF_UP));
      dto.setTipoAfectacionIgv("GRAVADO");

      itemDtos.add(dto);
    }

    // ==========================
    // COMPROBANTE
    // ==========================
    DocumentSendRequest comprobante = new DocumentSendRequest();
    comprobante.setCompSerie(doc.getSeries());
    comprobante.setCompNumero(
        Integer.parseInt(doc.getSequence()));
    comprobante.setCompFechaEmision(LocalDate.now().toString());
    comprobante.setCompPorcentajeIgv(
        new BigDecimal("18"));
    comprobante.setCompCondicionPago("CONTADO");
    comprobante.setCompMedioPago("EFECTIVO");
    comprobante.setMoneda(sale.getCurrencyCode());
    comprobante.setTipoDocumento(
        "01".equals(doc.getDocumentTypeSunat().getCode())
            ? "FACTURA"
            : "BOLETA"
    );
    comprobante.setTipoOperacion("VENTA_INTERNA");
    comprobante.setCliente(cliente);
    comprobante.setLsItemComprobante(itemDtos);

    // ==========================
    // WRAPPER
    // ==========================
    SunatSendRequest request = new SunatSendRequest();
    request.setEmpresa(empresa);
    request.setComprobante(comprobante);

    return request;
  }

  // =====================================================
  // RESPONSE
  // =====================================================

  private void processResponse(Document doc,
                               ResponseEntity<FacturacionResponse> response) {

    FacturacionResponse body = response.getBody();

    if (response.getStatusCode().is2xxSuccessful()
        && body != null
        && body.isSuccess()) {

      var data = body.getData();

      if ("ACEPTADO".equalsIgnoreCase(
          data.getDeclarationResultType())) {
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

      if ("ACEPTADO".equals(doc.getStatus())
          && data.getXmlBase64() != null
          && data.getCdrBase64() != null) {
        String[] urls = uploadXmlAndCdr(
            doc.getDocumentTypeSunat().getCode(),
            doc.getSeries(), doc.getSequence(),
            data.getXmlBase64(), data.getCdrBase64());
        doc.setXmlUrl(urls[0]);
        doc.setCdrUrl(urls[1]);
      }

    } else {
      doc.setStatus("ERROR");
      assert response.getBody() != null;
      doc.setSunatMessage(
          "HTTP Error: " + response.getBody().getMessage());
    }
  }

  private String[] uploadXmlAndCdr(String typeCode, String series,
      String sequence, String xmlBase64, String cdrBase64) {
    try {
      String xmlFileName = "XML-" + COMPANY_RUC + "-" + typeCode + "-"
          + series + "-" + sequence + ".xml";
      String cdrFileName = "CDR-" + COMPANY_RUC + "-" + typeCode + "-"
          + series + "-" + sequence + ".xml";

      File xmlFile = new File(
          System.getProperty("java.io.tmpdir") + "/" + xmlFileName);
      java.nio.file.Files.write(xmlFile.toPath(),
          java.util.Base64.getDecoder().decode(xmlBase64));

      File cdrFile = new File(
          System.getProperty("java.io.tmpdir") + "/" + cdrFileName);
      java.nio.file.Files.write(cdrFile.toPath(),
          java.util.Base64.getDecoder().decode(cdrBase64));

      String xmlUrl = googleDriveService.uploadFileWithPublicAccess(
          xmlFile, DRIVE_FOLDER_ID);
      String cdrUrl = googleDriveService.uploadFileWithPublicAccess(
          cdrFile, DRIVE_FOLDER_ID);

      xmlFile.delete();
      cdrFile.delete();

      return new String[]{xmlUrl, cdrUrl};
    } catch (Exception e) {
      log.error("Error subiendo XML/CDR a Drive: {}", e.getMessage(), e);
      return new String[]{null, null};
    }
  }
}
