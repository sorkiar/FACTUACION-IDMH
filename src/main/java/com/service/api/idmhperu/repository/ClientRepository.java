package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientRepository
    extends JpaRepository<Client, Long>,
    JpaSpecificationExecutor<Client> {
}
