package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.SaleItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

  List<SaleItem> findBySaleIdAndDeletedAtIsNull(Long saleId);
}
