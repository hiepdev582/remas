package com.hiepnn.remas.feature.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hiepnn.remas.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    List<UserRole> findByUserId(Integer userId);
}
