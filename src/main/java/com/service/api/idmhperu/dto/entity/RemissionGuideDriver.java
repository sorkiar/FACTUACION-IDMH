package com.service.api.idmhperu.dto.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "remission_guide_driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemissionGuideDriver {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "remission_guide_id", nullable = false)
  @JsonBackReference
  private RemissionGuide remissionGuide;

  /** Tipo de documento del conductor (ej: DNI) */
  @Column(name = "driver_doc_type", length = 5, nullable = false)
  private String driverDocType;

  @Column(name = "driver_doc_number", length = 20, nullable = false)
  private String driverDocNumber;

  @Column(name = "driver_first_name", length = 100, nullable = false)
  private String driverFirstName;

  @Column(name = "driver_last_name", length = 100, nullable = false)
  private String driverLastName;

  @Column(name = "driver_license_number", length = 30, nullable = false)
  private String driverLicenseNumber;

  @Column(name = "vehicle_plate", length = 20, nullable = false)
  private String vehiclePlate;

  // Auditoría
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by", length = 50, nullable = false, updatable = false)
  private String createdBy;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by", length = 50)
  private String updatedBy;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "deleted_by", length = 50)
  private String deletedBy;
}
