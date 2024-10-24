package com.company.companyapp.service;

import com.company.companyapp.model.User;
import com.company.companyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final String companyServiceUrl = "http://localhost:8081/api/company/sendOtp";
    private final String companyNamesUrl = "http://localhost:8081/api/company/allNames";
    private final String companyValidationUrl = "http://localhost:8081/api/company/validateOtp";

    @Override
    public String sendPhoneNumberToCompany(String phoneNumber) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("phoneNumber", phoneNumber);

        // Call the company's /sendOtp endpoint to generate and receive the OTP
        String otp = restTemplate.postForObject(companyServiceUrl, requestBody, String.class);

        System.out.println("User Service: Sent phone number to company, received OTP: " + otp);

        // Return the OTP for further processing (for example, testing purposes)
        return otp;
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare the request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("phoneNumber", phoneNumber);
        requestBody.put("otp", otp); // This will be the OTP entered by the user

        // Call the company's /validateOtp endpoint to check if the OTP is correct
        Boolean isValid = restTemplate.postForObject(companyValidationUrl, requestBody, Boolean.class);

        System.out.println("User Service: OTP verification result: " + isValid);
        return isValid != null && isValid;
    }


    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUser(String phoneNumber, User userDetails) {
        User user = getUserByPhoneNumber(phoneNumber);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    // New method to fetch all company names from Company Service
    public List<String> getCompanyNamesFromCompanyService() {
        RestTemplate restTemplate = new RestTemplate();
        // Fetch company names from company service
        List<String> companyNames = restTemplate.getForObject(companyNamesUrl, List.class);
        return companyNames;
    }
}
