package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.DniRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DniRecordRepository extends JpaRepository<DniRecord, String> {
}
