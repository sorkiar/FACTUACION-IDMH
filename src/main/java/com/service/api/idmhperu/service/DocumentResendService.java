package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.ApiResponse;

public interface DocumentResendService {
  ApiResponse<String> resendDocument(Long id);
  ApiResponse<String> resendCreditDebitNote(Long id);
}
