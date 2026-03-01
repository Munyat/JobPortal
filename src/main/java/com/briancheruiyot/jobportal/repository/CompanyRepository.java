package com.briancheruiyot.jobportal.repository;

import com.briancheruiyot.jobportal.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Optional
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
