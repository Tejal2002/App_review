package com.company.companyapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "company")
public class Company {

    @Id
    private String id;
    private String name;
    private String description;
    private int followers;
    private double rating; // New field for storing average rating

    // Constructors, getters, and setters
    public Company(String name, String description) {
        this.name = name;
        this.description = description;
        this.rating = 0.0; // Initialize the rating to zero or appropriate default
    }

    // Getters and Setters for the rating field
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }


}
