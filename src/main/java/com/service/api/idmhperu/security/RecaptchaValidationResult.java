package com.service.api.idmhperu.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecaptchaValidationResult {
  private boolean valid;
  private float score;
  private String action;
  private String reason;
}
