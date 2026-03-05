package com.briancheruiyot.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.briancheruiyot.jobportal.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);
}