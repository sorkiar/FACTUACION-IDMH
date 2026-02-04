package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
