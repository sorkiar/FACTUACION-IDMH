package com.service.api.idmhperu.dto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Catálogo No. 54 SUNAT: Bienes y servicios sujetos al SPOT (detracciones).
 * Fuente: RS N° 183-2004/SUNAT y modificaciones.
 */
@Entity
@Table(name = "detraction_code")
@Getter
@Setter
@NoArgsConstructor
public class DetractionCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Código de 3 dígitos del Catálogo 54 (ej: "019", "022"). */
  @Column(length = 3, nullable = false, unique = true)
  private String code;

  @Column(length = 500, nullable = false)
  private String description;

  /** Porcentaje de detracción (ej: 12.00 para 12%). */
  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal percentage;

  /** "BIEN" o "SERVICIO" según clasificación SUNAT. */
  @Column(length = 10, nullable = false)
  private String category;

  /** 1 = activo, 0 = inactivo. */
  @Column(nullable = false)
  private Integer status = 1;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
