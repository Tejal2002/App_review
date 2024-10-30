package com.company.companyapp.service;

import com.company.companyapp.model.User;
import com.company.companyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final String telegramBotToken = "token";
    private final String telegramChatId = "chatId";
    private final String companyNamesUrl = "http://localhost:8081/api/company/allNames";

    private String lastVerifiedPhoneNumber = null; // Track the last verified user

    @Override
    public String generateAndSendOtp(String phoneNumber) {
        try {
            String otp = String.format("%04d", new Random().nextInt(10000));

            User user = userRepository.findByPhoneNumber(phoneNumber);
            if (user != null) {
                user.setOtp(otp);
                user.setVerified(false); // Reset verification on new OTP request
            } else {
                user = new User(phoneNumber, otp);
            }
            userRepository.save(user);

            sendOtpToTelegram(phoneNumber, otp);
            return otp;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate OTP", e);
        }
    }

    private void sendOtpToTelegram(String phoneNumber, String otp) {
        String message = "Your OTP for verification is: " + otp;
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage?chat_id=" + telegramChatId + "&text=" + message;

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP to Telegram", e);
        }
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByPhoneNumber(phoneNumber));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Check if the user is already verified
            if (user.isVerified()) {
                return false; // Already verified, do not allow re-verification
            }
            if (user.getOtp().equals(otp)) {
                user.setVerified(true);
                lastVerifiedPhoneNumber = phoneNumber; // Set the last verified phone number
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getCompanyNamesFromCompanyService() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(companyNamesUrl, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve company names: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching company names", e);
        }
    }

    @Override
    public boolean isUserVerified(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);
        return user != null && user.isVerified();
    }

    public String getLastVerifiedPhoneNumber() {
        return lastVerifiedPhoneNumber;
    }
}