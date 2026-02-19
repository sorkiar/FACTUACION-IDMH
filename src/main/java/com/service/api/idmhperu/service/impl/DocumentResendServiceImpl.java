package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.repository.DocumentRepository;
import com.service.api.idmhperu.service.DocumentResendService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentResendServiceImpl implements DocumentResendService {

  private static final Set<String> NO_RESEND_STATUSES = Set.of("ACEPTADO", "PENDIENTE");

  private final DocumentRepository documentRepository;
  private final CreditDebitNoteRepository creditDebitNoteRepository;

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

    doc.setStatus("PENDIENTE");
    doc.setUpdatedBy("manual-resend");
    documentRepository.save(doc);

    return new ApiResponse<>("Documento "
        + doc.getSeries() + "-" + doc.getSequence()
        + " marcado como PENDIENTE. Será procesado en el próximo ciclo de envío.", null);
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

    note.setStatus("PENDIENTE");
    note.setUpdatedBy("manual-resend");
    creditDebitNoteRepository.save(note);

    return new ApiResponse<>("Nota "
        + note.getSeries() + "-" + note.getSequence()
        + " marcada como PENDIENTE. Será procesada en el próximo ciclo de envío.", null);
  }
}
