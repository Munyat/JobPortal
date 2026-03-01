package com.briancheruiyot.jobportal.service.impl;

import com.briancheruiyot.jobportal.dto.CompanyDto;
import com.briancheruiyot.jobportal.entity.Company;
import com.briancheruiyot.jobportal.repository.CompanyRepository;
import com.briancheruiyot.jobportal.service.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;

    // public CompanyServiceImpl(CompanyRepository companyRepository) {
    // this.companyRepository = companyRepository;
    // }

    @Override
    public List<CompanyDto> getAllCompanies() {
        List<Company> companyList = companyRepository.findAll();
        return companyList.stream().map(this::transformToDto).collect(Collectors.toList());
    }

    private CompanyDto transformToDto(Company company) {
        return new CompanyDto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt());
    }
}
