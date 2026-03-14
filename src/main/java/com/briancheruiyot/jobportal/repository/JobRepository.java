package com.briancheruiyot.jobportal.repository;

import com.briancheruiyot.jobportal.entity.Company;
import com.briancheruiyot.jobportal.entity.Job;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}