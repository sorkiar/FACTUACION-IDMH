package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.SaleItemRepository;
import com.service.api.idmhperu.repository.SaleRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import com.service.api.idmhperu.service.DocumentPdfService;
import com.service.api.idmhperu.service.GoogleDriveService;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentPdfServiceImpl implements DocumentPdfService {

  private final SaleRepository saleRepository;
  private final DocumentRepository documentRepository;
  private final SaleItemRepository saleItemRepository;
  private final GoogleDriveService googleDriveService;
  private final ConfigurationService configurationService;

  private static final String DRIVE_FOLDER_ID =
      "1ysDbcKhd4ZikJ17k4330pzFWH2_Vycpz";

  private static final String COMPANY_RUC = "20602592457";

  @Override
  @Transactional
  public void generatePdf(Long saleId) {
    try {
      Sale sale = saleRepository.findById(saleId)
          .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

      Document document = documentRepository.findBySaleId(saleId)
          .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));

      // ================================
      // Construir datos del reporte
      // ================================

      List<Map<String, Object>> dataList = new ArrayList<>();

      List<SaleItem> items = saleItemRepository.findBySaleId(saleId);

      if (items.isEmpty()) {
        throw new BusinessValidationException("La venta no tiene ítems registrados");
      }

      for (SaleItem item : items) {

        Map<String, Object> row = new HashMap<>();

        // ================= EMPRESA =================

        Map<String, String> config =
            configurationService.getGroup("empresa_emisora");

        row.put("empr_ruc", config.get("emprRuc"));
        row.put("empr_razon_social", config.get("emprRazonSocial"));
        row.put("empr_nombre_comercial", config.get("emprNombreComercial"));
        row.put("empr_direccion_fiscal", config.get("emprDireccionFiscal"));
        row.put("empr_telefono", config.get("emprTelefono"));
        row.put("empr_pagina_web", config.get("emprPaginaWeb"));
        row.put("empr_direccion_sucursal", null);
        row.put("empr_pdf_marca_agua", null);
        row.put("empr_pdf_texto_inferior", null);
        row.put("empr_pdf_eslogan", null);

        // ================= COMPROBANTE =================
        row.put("tico_descripcion",
            document.getDocumentTypeSunat().getCode().equals("01")
                ? "FACTURA ELECTRÓNICA"
                : "BOLETA ELECTRÓNICA");

        row.put("comp_numero_comprobante",
            document.getSeries() + "-" + document.getSequence());

        row.put("comp_fecha_emicion",
            Timestamp.valueOf(LocalDateTime.now()));

        row.put("comp_estado", "ACTIVO");
        row.put("comp_descuento_global", 0);

        row.put("comp_descripcion_cliente",
            resolveClientName(document, sale.getClient()));

        row.put("clie_numero_documento",
            sale.getClient().getDocumentNumber());

        row.put("comp_direccion_cliente",
            sale.getClient().getAddress());

        row.put("comp_condicion_pago", "Contado");

        row.put("priorizar_despacho", false);
        row.put("observaciones", sale.getObservations());

        // ================= MONEDA =================
        row.put("comp_descripcion_moneda", "SOLES");
        row.put("comp_simbolo_moneda", "S/");

        // ================= ITEM =================
        String sku;

        if (item.getProduct() != null) {
          sku = item.getProduct().getSku();
        } else if (item.getService() != null) {
          sku = item.getService().getSku();
        } else {
          sku = "SRV0000000";
        }

        row.put("itco_codigo_interno", sku);
        row.put("itco_descripcion_completa", item.getDescription());
        row.put("itco_cantidad", item.getQuantity());
        String unidad = "NIU";

        if (item.getUnitMeasure() != null &&
            item.getUnitMeasure().getCodeSunat() != null) {

          unidad = item.getUnitMeasure().getCodeSunat();
        }

        row.put("itco_unidad_medida", unidad);
        row.put("itco_precio_unitario", item.getUnitPrice());
        row.put("itco_descuento", BigDecimal.ZERO);
        row.put("itco_tipo_igv", 10);
        row.put("itco_igv", item.getTaxAmount());

        // ================= TRIBUTOS =================
        row.put("otros_tributos", 0.0);
        row.put("icbper", 0.0);

        // ================= QR =================
        row.put("comp_cadena_qr", buildQrString(document, sale));
        row.put("comp_codigo_hash", "");

        dataList.add(row);
      }

      // ================================
      // Compilar Jasper
      // ================================

      String template = document.getDocumentTypeSunat().getCode().equalsIgnoreCase("01")
          ? "/jasper/FacturaA4.jrxml"
          : "/jasper/BoletaA4.jrxml";

      InputStream inputStream =
          getClass().getResourceAsStream(template);

      JasperReport jasperReport =
          JasperCompileManager.compileReport(inputStream);

      JRBeanCollectionDataSource dataSource =
          new JRBeanCollectionDataSource(dataList);

      Map<String, Object> parameters = new HashMap<>();

      parameters.put("urlImagen",
          Objects.requireNonNull(getClass().getResource("/img/logo.png")).toString());

      JasperPrint jasperPrint =
          JasperFillManager.fillReport(
              jasperReport,
              parameters,
              dataSource
          );

      // ================================
      // 3Nombre archivo correcto
      // ================================

      String fileName =
          COMPANY_RUC + "-" +
              document.getDocumentTypeSunat().getCode() + "-" +
              document.getSeries() + "-" +
              document.getSequence() + ".pdf";

      File tempFile = new File(
          System.getProperty("java.io.tmpdir") + "/" + fileName);

      JasperExportManager.exportReportToPdfFile(
          jasperPrint,
          tempFile.getAbsolutePath()
      );

      // ================================
      // Subir a Drive
      // ================================

      String fileId =
          googleDriveService.uploadPdf(tempFile, DRIVE_FOLDER_ID);

      String driveUrl =
          "https://drive.google.com/file/d/" + fileId + "/view";

      document.setPdfUrl(driveUrl);
      documentRepository.save(document);

      tempFile.delete();
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al generar el PDF del comprobante: " + e.getMessage()
      );
    }
  }

  private String resolveClientName(Document document, Client client) {

    String documentType = document.getDocumentTypeSunat().getCode();

    // FACTURA
    if ("01".equals(documentType)) {

      if (client.getBusinessName() == null || client.getBusinessName().isBlank()) {
        throw new IllegalStateException(
            "El cliente no tiene razón social para emitir factura"
        );
      }

      return client.getBusinessName();
    }

    // BOLETA
    if ("03".equals(documentType)) {

      String fullName =
          (client.getFirstName() != null ? client.getFirstName() : "") +
              " " +
              (client.getLastName() != null ? client.getLastName() : "");

      return fullName.trim();
    }

    return "";
  }

  private String buildQrString(Document document, Sale sale) {

    if (document.getDocumentTypeSunat() == null) return "";

    return COMPANY_RUC + "|" +
        document.getDocumentTypeSunat().getCode() + "|" +
        document.getSeries() + "|" +
        document.getSequence() + "|" +
        sale.getTaxAmount() + "|" +
        sale.getTotalAmount() + "|" +
        sale.getSaleDate().toLocalDate() + "|" +
        sale.getClient().getDocumentType().getName() + "|" +
        sale.getClient().getDocumentNumber();
  }

}
