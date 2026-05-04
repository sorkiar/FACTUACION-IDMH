package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.TransferReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferReasonRepository extends JpaRepository<TransferReason, Long>,
    JpaSpecificationExecutor<TransferReason> {

}
