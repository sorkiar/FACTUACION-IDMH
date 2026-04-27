package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRepository extends JpaRepository<Profile, Long>,
    JpaSpecificationExecutor<Profile> {
}
