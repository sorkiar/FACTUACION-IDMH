package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository
    extends JpaRepository<Product, Long>,
    JpaSpecificationExecutor<Product> {
  boolean existsBySku(String sku);

  Optional<Product> findByIdAndStatusNot(Long id, Integer status);
}
