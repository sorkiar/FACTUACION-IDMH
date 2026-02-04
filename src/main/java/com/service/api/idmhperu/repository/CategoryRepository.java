package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository
    extends JpaRepository<Category, Long>,
    JpaSpecificationExecutor<Category> {
}
