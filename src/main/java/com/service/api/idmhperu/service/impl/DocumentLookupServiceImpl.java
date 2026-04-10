package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Configuration;
import com.service.api.idmhperu.dto.entity.DniRecord;
import com.service.api.idmhperu.dto.entity.RucRecord;
import com.service.api.idmhperu.dto.external.apiperu.ExternalApiResponse;
import com.service.api.idmhperu.dto.external.apiperu.ExternalDniData;
import com.service.api.idmhperu.dto.external.apiperu.ExternalRucData;
import com.service.api.idmhperu.dto.mapper.DniRecordMapper;
import com.service.api.idmhperu.dto.mapper.RucRecordMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DniRecordResponse;
import com.service.api.idmhperu.dto.response.RucRecordResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.repository.ConfigurationRepository;
import com.service.api.idmhperu.repository.DniRecordRepository;
import com.service.api.idmhperu.repository.RucRecordRepository;
import com.service.api.idmhperu.service.DocumentLookupService;
import com.service.api.idmhperu.util.JwtUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DocumentLookupServiceImpl implements DocumentLookupService {

  private static final String APIPERU_BASE_URL = "https://apiperu.dev/api";
  private static final String CONFIG_GROUP = "consulta_externa";
  private static final String CONFIG_KEY = "apiperu_token";
  private static final int CACHE_DAYS = 30;

  private final DniRecordRepository dniRecordRepository;
  private final RucRecordRepository rucRecordRepository;
  private final ConfigurationRepository configurationRepository;
  private final DniRecordMapper dniRecordMapper;
  private final RucRecordMapper rucRecordMapper;
  private final RestTemplate restTemplate = new RestTemplate(); // not injected — created inline

  @Override
  public ApiResponse<DniRecordResponse> queryDni(String dni) {
    Optional<DniRecord> cached = dniRecordRepository.findById(dni);
    if (cached.isPresent() && isFresh(cached.get().getCreatedAt())) {
      return new ApiResponse<>("Consulta DNI exitosa", dniRecordMapper.toResponse(cached.get()));
    }

    String token = resolveToken();
    HttpHeaders headers = buildHeaders(token);

    ResponseEntity<ExternalApiResponse<ExternalDniData>> response = restTemplate.exchange(
        APIPERU_BASE_URL + "/dni/" + dni,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<ExternalApiResponse<ExternalDniData>>() {}
    );

    ExternalApiResponse<ExternalDniData> body = response.getBody();
    if (body == null || body.getSuccess() == null) {
      throw new BusinessValidationException("La API externa no respondió correctamente");
    }
    if (!Boolean.TRUE.equals(body.getSuccess())) {
      throw new BusinessValidationException(body.getMessage());
    }

    String username = JwtUtils.extractUsernameFromContext();
    DniRecord record = dniRecordMapper.toEntity(body.getData());
    if (cached.isEmpty()) {
      record.setCreatedBy(username);
    } else {
      record.setCreatedAt(cached.get().getCreatedAt());
      record.setCreatedBy(cached.get().getCreatedBy());
      record.setUpdatedBy(username);
    }
    record = dniRecordRepository.save(record);

    return new ApiResponse<>("Consulta DNI exitosa", dniRecordMapper.toResponse(record));
  }

  @Override
  public ApiResponse<RucRecordResponse> queryRuc(String ruc) {
    Optional<RucRecord> cached = rucRecordRepository.findById(ruc);
    if (cached.isPresent() && isFresh(cached.get().getCreatedAt())) {
      return new ApiResponse<>("Consulta RUC exitosa", rucRecordMapper.toResponse(cached.get()));
    }

    String token = resolveToken();
    HttpHeaders headers = buildHeaders(token);

    ResponseEntity<ExternalApiResponse<ExternalRucData>> response = restTemplate.exchange(
        APIPERU_BASE_URL + "/ruc/" + ruc,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<ExternalApiResponse<ExternalRucData>>() {}
    );

    ExternalApiResponse<ExternalRucData> body = response.getBody();
    if (body == null || body.getSuccess() == null) {
      throw new BusinessValidationException("La API externa no respondió correctamente");
    }
    if (!Boolean.TRUE.equals(body.getSuccess())) {
      throw new BusinessValidationException(body.getMessage());
    }

    String username = JwtUtils.extractUsernameFromContext();
    RucRecord record = rucRecordMapper.toEntity(body.getData());
    if (cached.isEmpty()) {
      record.setCreatedBy(username);
    } else {
      record.setCreatedAt(cached.get().getCreatedAt());
      record.setCreatedBy(cached.get().getCreatedBy());
      record.setUpdatedBy(username);
    }
    record = rucRecordRepository.save(record);

    return new ApiResponse<>("Consulta RUC exitosa", rucRecordMapper.toResponse(record));
  }

  private String resolveToken() {
    Configuration config = configurationRepository
        .findByConfigGroupAndConfigKeyAndDeletedAtIsNull(CONFIG_GROUP, CONFIG_KEY)
        .orElseThrow(() -> new BusinessValidationException("Token de consulta no configurado"));
    String token = config.getConfigValue();
    if (token == null || token.isBlank()) {
      throw new BusinessValidationException("Token de consulta no configurado");
    }
    return token;
  }

  private HttpHeaders buildHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return headers;
  }

  private boolean isFresh(LocalDateTime createdAt) {
    return createdAt != null && createdAt.isAfter(LocalDateTime.now().minusDays(CACHE_DAYS));
  }
}
