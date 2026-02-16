package com.service.api.idmhperu.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.external.*;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.SaleItemRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final ConfigurationService configurationService;

  private final RestTemplate restTemplate = new RestTemplate();

  private static final String SUNAT_URL =
      "https://e-fact.facturacion-idmhperu.website/facturador/sunat/comprobante/sent";

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
                SUNAT_URL,
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

    } else {
      doc.setStatus("ERROR");
      doc.setSunatMessage(
          "HTTP Error: " + response.getStatusCode());
    }
  }
}
