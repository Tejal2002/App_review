package com.company.companyapp.controller;

import com.company.companyapp.DTO.ReviewRequest;
import com.company.companyapp.model.Company;
import com.company.companyapp.repository.CompanyRepository;
import com.company.companyapp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // Inject MongoTemplate
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    private final String userServiceUrl = "http://localhost:8080/api/users/reviewCompany";
    RestTemplate restTemplate = new RestTemplate();


    // New endpoint to fetch all company names
    @GetMapping("/allNames")
    public ResponseEntity<List<String>> getAllCompanyNames() {
        List<String> companyNames = companyService.getAllCompanyNames(); // Call the service to get company names
        return ResponseEntity.ok(companyNames);
    }

    private final Map<String, String> otpStore = new HashMap<>();

    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");

        // Hardcoded OTP (you can generate a dynamic one here if needed)
        String otp = "1234";

        // Store the OTP with the corresponding phone number
        otpStore.put(phoneNumber, otp);

        System.out.println("Company Service: Sending OTP " + otp + " to " + phoneNumber);
        return ResponseEntity.ok(otp);
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<Boolean> validateOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");

        // Retrieve the stored OTP for the given phone number
        String storedOtp = otpStore.get(phoneNumber);

        // Check if the stored OTP matches the provided OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            System.out.println("Company Service: OTP verification successful for " + phoneNumber);
            return ResponseEntity.ok(true);
        }

        System.out.println("Company Service: OTP verification failed for " + phoneNumber);
        return ResponseEntity.ok(false);
    }


    @PostMapping("/receiveReview")
    public ResponseEntity<String> receiveReview(@RequestBody ReviewRequest reviewRequest) {
        String companyName = reviewRequest.getCompanyName();
        String review = reviewRequest.getReview();
        String reviewer = reviewRequest.getReviewer();
        String phoneNumber = reviewRequest.getPhoneNumber();

        // Log the received data
        System.out.println("Received company: " + companyName);
        System.out.println("Received review: " + review);

        // Process the review and save it to the respective company
        String result = addReviewOfCompany(companyName, review, reviewer, phoneNumber);
        return ResponseEntity.ok(result);
    }

    private String addReviewOfCompany(String companyName, String feedback, String reviewer, String phoneNumber) {
        Optional<Company> optionalCompany = companyRepository.findByName(companyName);

        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();

            // Create a new Review object and set its fields
            Company.Review newReview = new Company.Review();
            newReview.setReviewer(reviewer);
            newReview.setPhoneNumber(phoneNumber);
            newReview.setComment(feedback);

            // Add the new review to the company's reviews list
            company.getReviews().add(newReview);

            // Save the updated company back to the database
            companyRepository.save(company);

            return "Review added successfully!";
        } else {
            // Company not found
            return "Company not found!";
        }
    }
}


