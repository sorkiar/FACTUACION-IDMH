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
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false, unique = true)
  private String sku;

  @Column(length = 200, nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "unit_measure_id", nullable = false)
  private UnitMeasure unitMeasure;

  @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal salePrice;

  @Column(name = "estimated_cost", precision = 12, scale = 2)
  private BigDecimal estimatedCost;

  private String brand;
  private String model;

  @Column(name = "short_description", length = 500, nullable = false)
  private String shortDescription;

  @Column(name = "technical_spec", columnDefinition = "TEXT", nullable = false)
  private String technicalSpec;

  @Column(name = "main_image_url", nullable = false)
  private String mainImageUrl;

  @Column(name = "technical_sheet_url")
  private String technicalSheetUrl;

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

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "deleted_by")
  private String deletedBy;
}
