package com.briancheruiyot.jobportal.company.controller.service;

import com.briancheruiyot.jobportal.dto.CompanyDto;
import com.briancheruiyot.jobportal.entity.Company;

import java.util.List;

public interface ICompanyService {

    List<CompanyDto> getAllCompanies();

    List<CompanyDto> getAllCompaniesForAdmin();

    void deleteCompanyById(Long id);

    boolean updateCompanyDetails(Long id, CompanyDto companyDto);

    boolean createCompany(CompanyDto companyDto);

}
