package com.company.companyapp.service;

import com.company.companyapp.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    String sendOtp(String phoneNumber);

    List<String> getAllCompanyNames();


}

