package com.service.api.idmhperu.service;

import com.service.api.idmhperu.security.RecaptchaValidationResult;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

  @Value("${recaptcha.secret.key}")
  private String secretKey;

  @Value("${recaptcha.min.score:0.5}")
  private float minScore;

  @Value("${recaptcha.bypass.enabled:false}")
  private boolean bypassEnabled;

  private final RestTemplate restTemplate = new RestTemplate();
  private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";

  public RecaptchaValidationResult validate(String token) {
    if (bypassEnabled) {
      return RecaptchaValidationResult.builder()
          .valid(true)
          .score(0.9f)
          .action("login")
          .reason("Bypass mode active")
          .build();
    }

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("secret", secretKey);
      body.add("response", token);

      HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

      @SuppressWarnings("rawtypes")
      ResponseEntity<Map> response = restTemplate.postForEntity(RECAPTCHA_URL, entity, Map.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        Boolean success = (Boolean) responseBody.get("success");

        if (Boolean.TRUE.equals(success)) {
          Double score = (Double) responseBody.get("score");
          // v2 does not return score — skip threshold check, success=true is enough
          if (score != null && score.floatValue() < minScore) {
            return RecaptchaValidationResult.builder()
                .valid(false)
                .score(score.floatValue())
                .action("login")
                .reason("reCAPTCHA score too low: " + score.floatValue() + " (min: " + minScore + ")")
                .build();
          }
          float finalScore = score != null ? score.floatValue() : 1.0f;
          return RecaptchaValidationResult.builder()
              .valid(true)
              .score(finalScore)
              .action("login")
              .reason("reCAPTCHA validation successful")
              .build();
        } else {
          @SuppressWarnings("unchecked")
          List<String> errorCodes = (List<String>) responseBody.get("error-codes");
          String reason = errorCodes != null && !errorCodes.isEmpty()
              ? "reCAPTCHA errors: " + String.join(", ", errorCodes)
              : "reCAPTCHA validation failed";
          return RecaptchaValidationResult.builder()
              .valid(false)
              .score(0.0f)
              .action("login")
              .reason(reason)
              .build();
        }
      }

      return RecaptchaValidationResult.builder()
          .valid(false)
          .score(0.0f)
          .action("login")
          .reason("reCAPTCHA API returned unexpected response")
          .build();

    } catch (Exception e) {
      return RecaptchaValidationResult.builder()
          .valid(false)
          .score(0.0f)
          .action("login")
          .reason("reCAPTCHA validation error: " + e.getMessage())
          .build();
    }
  }
}
