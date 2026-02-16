package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.SaleItem;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "product",
      "service",
  })
  List<SaleItem> findBySaleIdAndDeletedAtIsNull(Long saleId);

  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "product",
      "service",
  })
  List<SaleItem> findBySaleId(Long saleId);

  @Modifying
  @Query("DELETE FROM SaleItem si WHERE si.sale.id = :saleId")
  void deleteBySaleId(Long saleId);
}
