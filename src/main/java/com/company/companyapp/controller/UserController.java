package com.company.companyapp.controller;

import com.company.companyapp.model.User;
import com.company.companyapp.service.UserService;
import com.company.companyapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/sendPhoneNumber")
    public ResponseEntity<String> sendPhoneNumberToCompany(@RequestParam String phoneNumber) {
        String otp = userService.sendPhoneNumberToCompany(phoneNumber);
        return ResponseEntity.ok("OTP sent: " + otp); // For testing purposes
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        boolean isVerified = userService.verifyOtp(phoneNumber, otp);
        if (isVerified) {
            return ResponseEntity.ok("OTP verification successful");
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    @GetMapping("/companyMenu")
    public ResponseEntity<String> companyMenu() {
        List<String> companyNames = userService.getCompanyNamesFromCompanyService();

        StringBuilder formattedNames = new StringBuilder("Choose from the following companies:\n");
        for (int i = 0; i < companyNames.size(); i++) {
            formattedNames.append(i + 1).append(". ").append(companyNames.get(i)).append("\n");
        }

        // Return the formatted string
        return ResponseEntity.ok(formattedNames.toString());
    }



    @PostMapping("/reviewCompany")
    public String reviewCompany(@RequestParam(value = "companyName") String companyName,
                                @RequestParam(value = "review") String review,
                                @RequestParam(value = "reviewer") String reviewer,
                                @RequestParam(value = "phoneNumber") String phoneNumber) {

        // Create a Map to hold the review details
        Map<String, String> reviewDetails = new HashMap<>();
        reviewDetails.put("companyName", companyName);
        reviewDetails.put("review", review);
        reviewDetails.put("reviewer", reviewer);
        reviewDetails.put("phoneNumber", phoneNumber);

        // Create the HTTP request entity with the body (review details)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(reviewDetails, headers);

        // This is where RestTemplate is used to make the POST request
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8081/api/company/receiveReview",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // Return the response from the Company Microservice
        return "User gave response: " + response.getBody();
    }


}