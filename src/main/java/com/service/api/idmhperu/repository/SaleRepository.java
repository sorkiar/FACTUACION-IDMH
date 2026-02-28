package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Sale;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository
    extends JpaRepository<Sale, Long>,
    JpaSpecificationExecutor<Sale> {

  @Override
  @EntityGraph(attributePaths = {
      "client",
      "client.documentType",
      "client.personType",
      "items",
      "items.product",
      "items.service",
      "items.unitMeasure",
      "documents",
      "documents.documentTypeSunat",
      "payments",
      "payments.paymentMethod",
  })
  @NullMarked
  List<Sale> findAll(Specification<Sale> spec);

  @EntityGraph(attributePaths = {
      "client",
      "client.documentType",
      "client.personType",
      "items",
      "items.product",
      "items.service",
      "items.unitMeasure",
      "documents",
      "documents.documentTypeSunat",
      "payments",
      "payments.paymentMethod",
  })
  Optional<Sale> findByIdAndDeletedAtIsNull(Long id);

  long countBySaleDateBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end);

  @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s " +
      "WHERE s.saleDate BETWEEN :start AND :end AND s.deletedAt IS NULL")
  BigDecimal sumTotalAmountBySaleDateBetween(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @Query("SELECT FUNCTION('DAY', s.saleDate), COALESCE(SUM(s.totalAmount), 0) FROM Sale s " +
      "WHERE s.saleDate BETWEEN :start AND :end AND s.deletedAt IS NULL " +
      "GROUP BY FUNCTION('DAY', s.saleDate)")
  List<Object[]> sumTotalAmountGroupedByDay(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @EntityGraph(attributePaths = {
      "client",
      "client.documentType",
      "items",
      "items.product",
      "items.service",
      "documents",
      "documents.documentTypeSunat",
  })
  @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end " +
      "AND s.deletedAt IS NULL AND s.saleStatus <> 'BORRADOR' ORDER BY s.saleDate DESC")
  List<Sale> findForReport(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}
