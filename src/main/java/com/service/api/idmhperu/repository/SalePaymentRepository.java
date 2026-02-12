package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.SalePayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {

  List<SalePayment> findBySaleIdAndDeletedAtIsNull(Long saleId);
}
