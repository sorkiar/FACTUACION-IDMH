package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.RemissionGuideItem;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemissionGuideItemRepository extends JpaRepository<RemissionGuideItem, Long> {

  @EntityGraph(attributePaths = {"product"})
  List<RemissionGuideItem> findByRemissionGuideIdAndDeletedAtIsNull(Long remissionGuideId);
}
