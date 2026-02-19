package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteItem;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.CreditDebitNoteItemRepository;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import com.service.api.idmhperu.service.CreditDebitNotePdfService;
import com.service.api.idmhperu.service.GoogleDriveService;
import jakarta.transaction.Transactional;
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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditDebitNotePdfServiceImpl implements CreditDebitNotePdfService {

  private final CreditDebitNoteRepository noteRepository;
  private final CreditDebitNoteItemRepository noteItemRepository;
  private final GoogleDriveService googleDriveService;
  private final ConfigurationService configurationService;

  private static final String DRIVE_FOLDER_ID = "1ysDbcKhd4ZikJ17k4330pzFWH2_Vycpz";
  private static final String COMPANY_RUC = "20602592457";

  @Override
  @Transactional
  public void generatePdf(Long noteId) {
    try {
      CreditDebitNote note = noteRepository.findByIdAndDeletedAtIsNull(noteId)
          .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada"));

      List<CreditDebitNoteItem> items =
          noteItemRepository.findByCreditDebitNoteIdAndDeletedAtIsNull(noteId);

      if (items.isEmpty()) {
        throw new BusinessValidationException("La nota no tiene ítems registrados");
      }

      String origDocTypeCode = note.getOriginalDocument().getDocumentTypeSunat().getCode();
      String docRefTipo = "01".equals(origDocTypeCode)
          ? "FACTURA ELECTRÓNICA"
          : "BOLETA ELECTRÓNICA";
      String docRef = note.getOriginalDocument().getSeries()
          + "-" + note.getOriginalDocument().getSequence();
      String tipoNotaDesc = note.getCreditDebitNoteType().getName();

      List<Map<String, Object>> dataList = new ArrayList<>();

      for (CreditDebitNoteItem item : items) {

        Map<String, Object> row = new HashMap<>();

        // ================= EMPRESA =================
        Map<String, String> config = configurationService.getGroup("empresa_emisora");
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
        String tipoDocCode = note.getDocumentTypeSunat().getCode();
        row.put("tico_descripcion",
            "07".equals(tipoDocCode)
                ? "NOTA DE CRÉDITO ELECTRÓNICA"
                : "NOTA DE DÉBITO ELECTRÓNICA");

        row.put("comp_numero_comprobante",
            note.getSeries() + "-" + note.getSequence());
        row.put("comp_fecha_emicion", Timestamp.valueOf(LocalDateTime.now()));
        row.put("comp_estado", "ACTIVO");
        row.put("comp_descuento_global", 0);

        // ================= CLIENTE =================
        Client client = note.getSale().getClient();
        row.put("comp_descripcion_cliente", resolveClientName(origDocTypeCode, client));
        row.put("clie_numero_documento", client.getDocumentNumber());
        row.put("comp_direccion_cliente", client.getAddress());
        row.put("comp_condicion_pago", "Contado");
        row.put("priorizar_despacho", false);

        // ================= NOTA DE REFERENCIA =================
        row.put("doc_referencia", docRef);
        row.put("doc_referencia_tipo", docRefTipo);
        row.put("tipo_nota_desc", tipoNotaDesc);
        row.put("observaciones", note.getReason());

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
        if (item.getUnitMeasure() != null && item.getUnitMeasure().getCodeSunat() != null) {
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
        row.put("comp_cadena_qr", buildQrString(note));
        row.put("comp_codigo_hash", "");

        dataList.add(row);
      }

      // Compilar Jasper
      InputStream inputStream =
          getClass().getResourceAsStream("/jasper/NotaCreditoDebitoA4.jrxml");

      JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("urlImagen",
          Objects.requireNonNull(getClass().getResource("/img/logo.png")).toString());

      JasperPrint jasperPrint =
          JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      // Nombre archivo
      String fileName = COMPANY_RUC + "-"
          + note.getDocumentTypeSunat().getCode() + "-"
          + note.getSeries() + "-"
          + note.getSequence() + ".pdf";

      File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
      JasperExportManager.exportReportToPdfFile(jasperPrint, tempFile.getAbsolutePath());

      // Subir a Drive
      String fileId = googleDriveService.uploadPdf(tempFile, DRIVE_FOLDER_ID);
      String driveUrl = "https://drive.google.com/file/d/" + fileId + "/view";

      note.setPdfUrl(driveUrl);
      noteRepository.save(note);

      tempFile.delete();

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al generar el PDF de la nota: " + e.getMessage());
    }
  }

  private String resolveClientName(String origDocTypeCode, Client client) {
    if ("01".equals(origDocTypeCode)) {
      if (client.getBusinessName() == null || client.getBusinessName().isBlank()) {
        throw new IllegalStateException(
            "El cliente no tiene razón social para emitir factura");
      }
      return client.getBusinessName();
    }
    String fullName =
        (client.getFirstName() != null ? client.getFirstName() : "") +
            " " +
            (client.getLastName() != null ? client.getLastName() : "");
    return fullName.trim();
  }

  private String buildQrString(CreditDebitNote note) {
    Sale sale = note.getSale();
    return COMPANY_RUC + "|" +
        note.getDocumentTypeSunat().getCode() + "|" +
        note.getSeries() + "|" +
        note.getSequence() + "|" +
        note.getTaxAmount() + "|" +
        note.getTotalAmount() + "|" +
        note.getIssueDate().toLocalDate() + "|" +
        sale.getClient().getDocumentType().getName() + "|" +
        sale.getClient().getDocumentNumber();
  }
}
