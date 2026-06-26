package com.hiepnn.remas.feature.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hiepnn.remas.entity.Role;

import java.util.Optional;
import com.hiepnn.remas.common.constant.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);
}
