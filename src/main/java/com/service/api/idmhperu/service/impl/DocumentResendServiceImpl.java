package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.RemissionGuide;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.job.SunatDocumentJobService;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.repository.RemissionGuideRepository;
import com.service.api.idmhperu.service.CreditDebitNotePdfService;
import com.service.api.idmhperu.service.DocumentPdfService;
import com.service.api.idmhperu.service.DocumentResendService;
import com.service.api.idmhperu.service.RemissionGuidePdfService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentResendServiceImpl implements DocumentResendService {

  private static final Set<String> NO_RESEND_STATUSES = Set.of("ACEPTADO");

  private final DocumentRepository documentRepository;
  private final CreditDebitNoteRepository creditDebitNoteRepository;
  private final RemissionGuideRepository remissionGuideRepository;
  private final SunatDocumentJobService jobService;
  private final DocumentPdfService documentPdfService;
  private final CreditDebitNotePdfService creditDebitNotePdfService;
  private final RemissionGuidePdfService remissionGuidePdfService;

  @Override
  @Transactional
  public ApiResponse<String> resendDocument(Long id) {

    Document doc = documentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + id));

    if (NO_RESEND_STATUSES.contains(doc.getStatus())) {
      throw new BusinessValidationException(
          "No se puede reenviar un documento en estado '" + doc.getStatus() + "'. "
              + "Solo se permite reenviar documentos en estado ERROR o RECHAZADO.");
    }

    jobService.sendDocumentNow(doc);

    return new ApiResponse<>("Documento "
        + doc.getSeries() + "-" + doc.getSequence()
        + " enviado a SUNAT. Estado: " + doc.getStatus()
        + (doc.getSunatMessage() != null ? " — " + doc.getSunatMessage() : ""), null);
  }

  @Override
  @Transactional
  public ApiResponse<String> resendCreditDebitNote(Long id) {

    CreditDebitNote note = creditDebitNoteRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada con id: " + id));

    if (NO_RESEND_STATUSES.contains(note.getStatus())) {
      throw new BusinessValidationException(
          "No se puede reenviar una nota en estado '" + note.getStatus() + "'. "
              + "Solo se permite reenviar notas en estado ERROR o RECHAZADO.");
    }

    jobService.sendCreditDebitNoteNow(note);

    return new ApiResponse<>("Nota "
        + note.getSeries() + "-" + note.getSequence()
        + " enviada a SUNAT. Estado: " + note.getStatus()
        + (note.getSunatMessage() != null ? " — " + note.getSunatMessage() : ""), null);
  }

  @Override
  @Transactional
  public ApiResponse<String> resendRemissionGuide(Long id) {

    RemissionGuide guide = remissionGuideRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Guía de remisión no encontrada con id: " + id));

    if (NO_RESEND_STATUSES.contains(guide.getStatus())) {
      throw new BusinessValidationException(
          "No se puede reenviar una guía en estado '" + guide.getStatus() + "'. "
              + "Solo se permite reenviar guías en estado ERROR o RECHAZADO.");
    }

    jobService.sendRemissionGuideNow(guide);

    return new ApiResponse<>("Guía "
        + guide.getSeries() + "-" + guide.getSequence()
        + " enviada a SUNAT. Estado: " + guide.getStatus()
        + (guide.getSunatMessage() != null ? " — " + guide.getSunatMessage() : ""), null);
  }

  @Override
  @Transactional
  public ApiResponse<String> regenerateDocumentPdf(Long id) {

    Document doc = documentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + id));

    documentPdfService.generatePdf(doc.getSale().getId());

    return new ApiResponse<>("PDF del documento "
        + doc.getSeries() + "-" + doc.getSequence()
        + " regenerado y actualizado correctamente.", null);
  }

  @Override
  @Transactional
  public ApiResponse<String> regenerateCreditDebitNotePdf(Long id) {

    CreditDebitNote note = creditDebitNoteRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada con id: " + id));

    creditDebitNotePdfService.generatePdf(note.getId());

    return new ApiResponse<>("PDF de la nota "
        + note.getSeries() + "-" + note.getSequence()
        + " regenerado y actualizado correctamente.", null);
  }

  @Override
  @Transactional
  public ApiResponse<String> regenerateRemissionGuidePdf(Long id) {

    RemissionGuide guide = remissionGuideRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Guía de remisión no encontrada con id: " + id));

    remissionGuidePdfService.generatePdf(guide.getId());

    return new ApiResponse<>("PDF de la guía "
        + guide.getSeries() + "-" + guide.getSequence()
        + " regenerado y actualizado correctamente.", null);
  }
}
