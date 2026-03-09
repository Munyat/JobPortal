package com.briancheruiyot.jobportal.repository;

import com.briancheruiyot.jobportal.entity.Company;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository // Optional
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT DISTINCT c FROM Company c JOIN FETCH c.jobs j WHERE j.status = :status")
    List<Company> findAllWithJobsByStatus(@Param("status") String status);

    List<Company> fetchCompaniesWithJobsByStatus(@Param("status") String status);

    @Query(value = "SELECT DISTINCT c.* FROM companies c JOIN jobs j ON c.id = j.company_id WHERE j.status = ?", nativeQuery = true)
    List<Company> findAllWithJobsByStatusNative(String status);

    List<Company> fetchCompaniesWithJobsByStatusNative(String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int updateCompanyDetails(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("logo") String logo,
            @Param("industry") String industry,
            @Param("size") String size,
            @Param("rating") BigDecimal rating,
            @Param("locations") String locations,
            @Param("founded") Integer founded,
            @Param("description") String description,
            @Param("employees") Integer employees,
            @Param("website") String website);

}
