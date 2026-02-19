package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.job.SunatDocumentJobService;
import com.service.api.idmhperu.service.DocumentResendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentResendController {

  private final DocumentResendService resendService;
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
   * Dispara manualmente el job de envío a SUNAT para procesar todos los documentos
   * y notas en estado PENDIENTE de forma inmediata, sin esperar el ciclo programado.
   */
  @PostMapping("/admin/trigger-sunat-job")
  public ApiResponse<String> triggerSunatJob() {
    jobService.sendPendingDocuments();
    return new ApiResponse<>("Job de envío a SUNAT ejecutado correctamente.", null);
  }
}
