package com.hiepnn.remas.feature.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hiepnn.remas.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
