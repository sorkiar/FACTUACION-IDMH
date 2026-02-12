package com.service.api.idmhperu.dto.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private Client client;

  @Column(name = "sale_status", length = 20, nullable = false)
  private String saleStatus;

  @Column(name = "payment_status", length = 20, nullable = false)
  private String paymentStatus;

  @Column(name = "subtotal_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal subtotalAmount;

  @Column(name = "discount_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal discountAmount;

  @Column(name = "tax_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal taxAmount;

  @Column(name = "total_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "currency_code", length = 4, nullable = false)
  private String currencyCode;

  @Column(name = "tax_percentage", precision = 5, scale = 2, nullable = false)
  private BigDecimal taxPercentage;

  @Column(name = "sale_date", nullable = false)
  private LocalDateTime saleDate;

  @Column(columnDefinition = "TEXT")
  private String observations;

  @OneToMany(
      mappedBy = "sale",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @JsonManagedReference
  private List<SaleItem> items;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<SalePayment> payments;

  // Audit
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
