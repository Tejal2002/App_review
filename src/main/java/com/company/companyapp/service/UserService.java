package com.company.companyapp.service;

import java.util.List;

public interface UserService {
    String generateAndSendOtp(String phoneNumber);
    boolean verifyOtp(String phoneNumber, String otp);
    boolean isUserVerified(String phoneNumber); // New method to check user verification status
    List<String> getCompanyNamesFromCompanyService(); // New method to fetch company names
}
