package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.RemissionGuideDriver;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemissionGuideDriverRepository extends JpaRepository<RemissionGuideDriver, Long> {

  List<RemissionGuideDriver> findByRemissionGuideIdAndDeletedAtIsNull(Long remissionGuideId);
}
