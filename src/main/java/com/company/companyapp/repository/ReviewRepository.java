package com.company.companyapp.repository;

import com.company.companyapp.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByCompanyId(String companyId);  // Fetch reviews by companyId
}
