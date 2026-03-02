package com.briancheruiyot.jobportal.company.controller.service;

import com.briancheruiyot.jobportal.dto.CompanyDto;
import com.briancheruiyot.jobportal.entity.Company;

import java.util.List;

public interface ICompanyService {

    List<CompanyDto> getAllCompanies();

}
