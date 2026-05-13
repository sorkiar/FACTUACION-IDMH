package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MenuRepository extends JpaRepository<Menu, Long> {

  @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.parent WHERE m.menuType = :menuType ORDER BY m.sortOrder ASC, m.id ASC")
  List<Menu> findAllByMenuTypeWithParent(String menuType);

  @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.parent ORDER BY m.sortOrder ASC, m.id ASC")
  List<Menu> findAllWithParent();

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
