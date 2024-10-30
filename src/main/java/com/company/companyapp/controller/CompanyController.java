package com.company.companyapp.controller;

import com.company.companyapp.model.Company;
import com.company.companyapp.model.Review;
import com.company.companyapp.repository.CompanyRepository;
import com.company.companyapp.repository.ReviewRepository;
import com.company.companyapp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Endpoint to fetch all company names
    @GetMapping("/allNames")
    public ResponseEntity<List<String>> getAllCompanyNames() {
        List<String> companyNames = companyService.getAllCompanyNames();
        return ResponseEntity.ok(companyNames);
    }

    // Endpoint to receive a review for a company
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

            // Create and save the review
            Review newReview = new Review(companyId, reviewer, phoneNumber, review, rating);
            reviewRepository.save(newReview);

            // Update the company's average rating
            updateCompanyRating(companyId);

            return ResponseEntity.ok("Review added successfully!");
        } else {
            return ResponseEntity.badRequest().body("Company not found");
        }
    }

    private void updateCompanyRating(String companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        double newAverageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Company company = companyRepository.findById(companyId).orElseThrow();
        company.setRating(newAverageRating);
        companyRepository.save(company);
    }

    @GetMapping("/{companyId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByCompany(@PathVariable String companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);

        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reviews);
    }

    // Modified addCompany endpoint to prevent duplicate companies
    @PostMapping("/addCompany")
    public ResponseEntity<String> addCompany(@RequestBody Company companyDetails) {
        Optional<Company> existingCompany = companyRepository.findByName(companyDetails.getName());
        if (existingCompany.isPresent()) {
            return ResponseEntity.badRequest().body("Company already exists!");
        }

        companyRepository.save(companyDetails);
        return ResponseEntity.ok("Company added successfully!");
    }
}
