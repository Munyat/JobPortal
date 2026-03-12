package com.briancheruiyot.jobportal.repository;

import com.briancheruiyot.jobportal.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}