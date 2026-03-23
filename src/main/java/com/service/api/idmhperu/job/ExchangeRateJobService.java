package com.service.api.idmhperu.job;

import com.service.api.idmhperu.dto.entity.ExchangeRate;
import com.service.api.idmhperu.repository.ExchangeRateRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateJobService {

  /**
   * Fuente primaria: API pública JSON con soporte de fechas históricas.
   * Patrón: GET https://free.e-api.net.pe/tipo-cambio/{YYYY-MM-DD}.json
   */
  private static final String EAPI_URL_TEMPLATE =
      "https://free.e-api.net.pe/tipo-cambio/{date}.json";

  /**
   * Fuente de respaldo: archivo TXT oficial de SUNAT, solo retorna el tipo de cambio del día.
   * Formato de respuesta: DD/MM/YYYY|compra|venta|
   */
  private static final String SUNAT_TXT_URL =
      "https://www.sunat.gob.pe/a/txt/tipoCambio.txt";

  private static final String CONFIG_GROUP = "tipo_cambio";

  private final ExchangeRateRepository exchangeRateRepository;
  private final ConfigurationService configurationService;

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Corre cada minuto. Cuando la hora actual coincide con fetch_hour (config),
   * registra el tipo de cambio del día si aún no existe.
   */
  @Scheduled(fixedRate = 60_000)
  public void scheduledFetch() {
    try {
      Map<String, String> config = configurationService.getGroup(CONFIG_GROUP);
      int configHour = Integer.parseInt(config.getOrDefault("fetch_hour", "9")) % 24;
      if (LocalTime.now().getHour() != configHour) return;
      fetchTodayIfMissing();
    } catch (Exception e) {
      log.error("Error en scheduled exchange rate fetch: {}", e.getMessage(), e);
    }
  }

  /**
   * Al iniciar la aplicación, verifica si el tipo de cambio de hoy ya está registrado.
   * Si no, lo obtiene de inmediato (cubre el caso de despliegue tardío).
   */
  @EventListener(ApplicationReadyEvent.class)
  public void fetchOnStartup() {
    try {
      log.info("Verificando tipo de cambio al inicio...");
      fetchTodayIfMissing();
    } catch (Exception e) {
      log.error("Error al obtener tipo de cambio al inicio: {}", e.getMessage(), e);
    }
  }

  private void fetchTodayIfMissing() {
    LocalDate today = LocalDate.now();
    if (exchangeRateRepository.existsByDateAndType(today, "C")) {
      log.debug("Tipo de cambio para {} ya registrado", today);
      return;
    }
    fetchAndStore(today);
  }

  /**
   * Intenta obtener el tipo de cambio para la fecha dada.
   * Fuente primaria: eApi (JSON). Fallback: TXT de SUNAT (solo para hoy).
   */
  private void fetchAndStore(LocalDate date) {
    log.info("Obteniendo tipo de cambio para {}", date);

    try {
      String url = EAPI_URL_TEMPLATE.replace("{date}", date.toString());
      ResponseEntity<EApiResponse> response =
          restTemplate.getForEntity(url, EApiResponse.class);

      EApiResponse body = response.getBody();
      if (body == null || body.getCompra() == null || body.getVenta() == null) {
        log.warn("Respuesta vacía o incompleta de eApi para {}. Intentando fuente alternativa...", date);
        if (date.equals(LocalDate.now())) fetchFromSunatTxt(date);
        return;
      }

      saveRate(date, body.getCompra(), "C");
      saveRate(date, body.getVenta(), "V");
      log.info("Tipo de cambio para {} registrado: compra={}, venta={}", date, body.getCompra(), body.getVenta());

    } catch (Exception e) {
      log.warn("Error obteniendo tipo de cambio de eApi para {}: {}. Intentando fuente alternativa...",
          date, e.getMessage());
      if (date.equals(LocalDate.now())) fetchFromSunatTxt(date);
    }
  }

  /**
   * Fallback: lee el archivo TXT oficial de SUNAT.
   * Solo retorna el tipo de cambio del día actual.
   * Formato: DD/MM/YYYY|compra|venta|
   */
  private void fetchFromSunatTxt(LocalDate date) {
    try {
      log.info("Obteniendo tipo de cambio desde TXT SUNAT para {}", date);
      ResponseEntity<String> response = restTemplate.getForEntity(SUNAT_TXT_URL, String.class);

      String txt = response.getBody();
      if (txt == null || txt.isBlank()) {
        log.warn("Respuesta vacía del TXT de SUNAT");
        return;
      }

      String[] parts = txt.trim().split("\\|");
      if (parts.length < 3) {
        log.warn("Formato inesperado del TXT de SUNAT: {}", txt);
        return;
      }

      BigDecimal compra = new BigDecimal(parts[1].trim());
      BigDecimal venta = new BigDecimal(parts[2].trim());

      saveRate(date, compra, "C");
      saveRate(date, venta, "V");
      log.info("Tipo de cambio para {} registrado desde TXT SUNAT: compra={}, venta={}", date, compra, venta);

    } catch (Exception e) {
      log.error("Error obteniendo tipo de cambio del TXT SUNAT: {}", e.getMessage(), e);
    }
  }

  private void saveRate(LocalDate date, BigDecimal value, String type) {
    if (exchangeRateRepository.existsByDateAndType(date, type)) {
      log.debug("Tipo de cambio para {} tipo {} ya existe, ignorando", date, type);
      return;
    }
    ExchangeRate rate = new ExchangeRate();
    rate.setDate(date);
    rate.setValue(value);
    rate.setType(type);
    try {
      exchangeRateRepository.save(rate);
    } catch (DataIntegrityViolationException e) {
      log.debug("Tipo de cambio para {} tipo {} ya existe (concurrencia), ignorando", date, type);
    }
  }

  // ──────────────────────────────────────────────
  // DTO interno para la respuesta de eApi
  // ──────────────────────────────────────────────

  @Getter
  @Setter
  static class EApiResponse {
    /** Fecha en formato YYYY-MM-DD */
    private String fecha;
    /** Tipo de cambio referencial SUNAT */
    private BigDecimal sunat;
    /** Tipo de cambio compra */
    private BigDecimal compra;
    /** Tipo de cambio venta */
    private BigDecimal venta;
  }
}
