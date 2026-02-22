package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.FileDownload;
import com.service.api.idmhperu.dto.response.SunatDocumentSummaryResponse;
import com.service.api.idmhperu.job.SunatDocumentJobService;
import com.service.api.idmhperu.service.DocumentFileService;
import com.service.api.idmhperu.service.DocumentResendService;
import com.service.api.idmhperu.service.SunatDocumentListService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentResendController {

  private final DocumentResendService resendService;
  private final DocumentFileService fileService;
  private final SunatDocumentListService listService;
  private final SunatDocumentJobService jobService;

  /**
   * Reenvía manualmente una factura o boleta que tenga estado ERROR o RECHAZADO.
   * Resetea el estado a PENDIENTE para que el job lo reprocese.
   */
  @PostMapping("/documents/{id}/resend")
  public ApiResponse<String> resendDocument(@PathVariable Long id) {
    return resendService.resendDocument(id);
  }

  /**
   * Reenvía manualmente una nota de crédito o débito que tenga estado ERROR o RECHAZADO.
   * Resetea el estado a PENDIENTE para que el job lo reprocese.
   */
  @PostMapping("/credit-debit-notes/{id}/resend")
  public ApiResponse<String> resendCreditDebitNote(@PathVariable Long id) {
    return resendService.resendCreditDebitNote(id);
  }

  /**
   * Reenvía manualmente una guía de remisión que tenga estado ERROR o RECHAZADO.
   * Resetea el estado a PENDIENTE para que el job lo reprocese.
   */
  @PostMapping("/remission-guides/{id}/resend")
  public ApiResponse<String> resendRemissionGuide(@PathVariable Long id) {
    return resendService.resendRemissionGuide(id);
  }

  // ========================================================================================
  // LISTADO UNIFICADO SUNAT
  // ========================================================================================

  /**
   * Lista todos los comprobantes electrónicos (facturas, boletas, notas de crédito/débito
   * y guías de remisión) ordenados por fecha de emisión descendente.
   *
   * @param status filtro opcional: PENDIENTE | ACEPTADO | RECHAZADO | ERROR
   */
  @GetMapping("/sunat/documents")
  public ApiResponse<List<SunatDocumentSummaryResponse>> listSunatDocuments(
      @RequestParam(required = false) String status) {
    return new ApiResponse<>(null, listService.listAll(status));
  }

  // ========================================================================================
  // DESCARGA DE ARCHIVOS — DOCUMENTOS (facturas / boletas)
  // ========================================================================================

  @GetMapping("/documents/{id}/xml")
  public ResponseEntity<byte[]> getDocumentXml(@PathVariable Long id) {
    return toFileResponse(fileService.getDocumentXml(id));
  }

  @GetMapping("/documents/{id}/cdr")
  public ResponseEntity<byte[]> getDocumentCdr(@PathVariable Long id) {
    return toFileResponse(fileService.getDocumentCdr(id));
  }

  @GetMapping("/documents/{id}/pdf")
  public ResponseEntity<byte[]> getDocumentPdf(@PathVariable Long id) {
    return toFileResponse(fileService.getDocumentPdf(id));
  }

  // ========================================================================================
  // DESCARGA DE ARCHIVOS — NOTAS DE CRÉDITO / DÉBITO
  // ========================================================================================

  @GetMapping("/credit-debit-notes/{id}/xml")
  public ResponseEntity<byte[]> getCreditDebitNoteXml(@PathVariable Long id) {
    return toFileResponse(fileService.getCreditDebitNoteXml(id));
  }

  @GetMapping("/credit-debit-notes/{id}/cdr")
  public ResponseEntity<byte[]> getCreditDebitNoteCdr(@PathVariable Long id) {
    return toFileResponse(fileService.getCreditDebitNoteCdr(id));
  }

  @GetMapping("/credit-debit-notes/{id}/pdf")
  public ResponseEntity<byte[]> getCreditDebitNotePdf(@PathVariable Long id) {
    return toFileResponse(fileService.getCreditDebitNotePdf(id));
  }

  // ========================================================================================
  // DESCARGA DE ARCHIVOS — GUÍAS DE REMISIÓN
  // ========================================================================================

  @GetMapping("/remission-guides/{id}/xml")
  public ResponseEntity<byte[]> getRemissionGuideXml(@PathVariable Long id) {
    return toFileResponse(fileService.getRemissionGuideXml(id));
  }

  @GetMapping("/remission-guides/{id}/cdr")
  public ResponseEntity<byte[]> getRemissionGuideCdr(@PathVariable Long id) {
    return toFileResponse(fileService.getRemissionGuideCdr(id));
  }

  @GetMapping("/remission-guides/{id}/pdf")
  public ResponseEntity<byte[]> getRemissionGuidePdf(@PathVariable Long id) {
    return toFileResponse(fileService.getRemissionGuidePdf(id));
  }

  // ========================================================================================
  // JOB MANUAL
  // ========================================================================================

  /**
   * Dispara manualmente el job de envío a SUNAT para procesar todos los documentos
   * y notas en estado PENDIENTE de forma inmediata, sin esperar el ciclo programado.
   */
  @PostMapping("/admin/trigger-sunat-job")
  public ApiResponse<String> triggerSunatJob() {
    jobService.sendPendingDocuments();
    return new ApiResponse<>("Job de envío a SUNAT ejecutado correctamente.", null);
  }

  // ========================================================================================
  // HELPER
  // ========================================================================================

  private ResponseEntity<byte[]> toFileResponse(FileDownload file) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
        .contentType(MediaType.parseMediaType(file.contentType()))
        .body(file.content());
  }
}
