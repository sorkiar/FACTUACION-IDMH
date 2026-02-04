package com.service.api.idmhperu.dto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 30, nullable = false, unique = true)
  private String sku;

  @Column(length = 150, nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_category_id", nullable = false)
  private ServiceCategory serviceCategory;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "charge_unit_id", nullable = false)
  private ChargeUnit chargeUnit;

  private BigDecimal price;

  @Column(name = "estimated_time", length = 50)
  private String estimatedTime;

  @Column(name = "expected_deliverable", length = 150)
  private String expectedDeliverable;

  @Column(columnDefinition = "TEXT")
  private String includes;

  @Column(columnDefinition = "TEXT")
  private String excludes;

  @Column(columnDefinition = "TEXT")
  private String conditions;

  private Integer requiresMaterials;
  private Integer requiresPlan;

  @Column(name = "short_description", nullable = false)
  private String shortDescription;

  @Column(name = "detailed_description", nullable = false, columnDefinition = "TEXT")
  private String detailedDescription;

  private Integer status;

  // Auditoría
  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(name = "updated_by", length = 50)
  private String updatedBy;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "deleted_by", length = 50)
  private String deletedBy;
}
