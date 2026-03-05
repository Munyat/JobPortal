package com.briancheruiyot.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.briancheruiyot.jobportal.entity.JobPortalUser;

public interface JobPortalUserRepository extends JpaRepository<JobPortalUser, Long> {

    Optional<JobPortalUser> readUserByEmailOrMobileNumber(String email, String mobileNumber);

    Optional<JobPortalUser> findJobPortalUserByEmail(String email);
}
