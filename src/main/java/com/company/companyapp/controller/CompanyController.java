package com.company.companyapp.controller;

import com.company.companyapp.model.Company;
import com.company.companyapp.model.Review;
import com.company.companyapp.repository.CompanyRepository;
import com.company.companyapp.repository.ReviewRepository;
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

        String otp = "1234";
        otpStore.clear();
        otpStore.put(phoneNumber, otp);

        System.out.println("Company Service: Sending OTP " + otp + " to " + phoneNumber);
        return ResponseEntity.ok(otp);
    }


    @PostMapping("/validateOtp")
    public ResponseEntity<Boolean> validateOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");

        // Retrieve the stored OTP for the given phone number
        String storedOtp = otpStore.get(phoneNumber); // This gets the OTP that was sent

        // Check if the stored OTP matches the provided OTP and the OTP is hardcoded
        if (storedOtp != null && storedOtp.equals(otp) && otp.equals("1234")) {
            System.out.println("Company Service: OTP verification successful for " + phoneNumber);
            return ResponseEntity.ok(true);
        }

        System.out.println("Company Service: OTP verification failed for " + phoneNumber);
        return ResponseEntity.ok(false);
    }


    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping("/receiveReview")
    public ResponseEntity<String> receiveReview(@RequestBody Map<String, String> reviewDetails) {
        String companyName = reviewDetails.get("companyName");
        String review = reviewDetails.get("review");
        String reviewer = reviewDetails.get("reviewer");
        String phoneNumber = reviewDetails.get("phoneNumber");
        int rating = Integer.parseInt(reviewDetails.get("rating"));

        Optional<Company> optionalCompany = companyRepository.findByName(companyName);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            String companyId = company.getId();

            // Create and save the review in the reviews collection
            Review newReview = new Review(companyId, reviewer, phoneNumber, review, rating);
            reviewRepository.save(newReview);

            return ResponseEntity.ok("Review added successfully!");
        } else {
            return ResponseEntity.badRequest().body("Company not found");
        }
    }

    // New endpoint to get reviews by company
    @GetMapping("/reviews/{companyId}")
    public ResponseEntity<List<Review>> getReviewsByCompany(@PathVariable String companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        return ResponseEntity.ok(reviews);
    }
}