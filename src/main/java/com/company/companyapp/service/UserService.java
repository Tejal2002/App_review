package com.company.companyapp.service;

import com.company.companyapp.model.User;

public interface UserService {
    String sendPhoneNumberToCompany(String phoneNumber);
    boolean verifyOtp(String phoneNumber, String otp);
    User getUserByPhoneNumber(String phoneNumber);
    User updateUser(String phoneNumber, User userDetails);
}