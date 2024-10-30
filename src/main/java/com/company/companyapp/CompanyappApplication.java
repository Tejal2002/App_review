package com.company.companyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class CompanyappApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyappApplication.class, args);
//
//		try {
//			// Register the bot using the new TelegramBotsApi setup
//			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//			botsApi.registerBot(new TelegramBot()); // This should be injected, not instantiated directly
//		} catch (TelegramApiException e) {
//			e.printStackTrace();
//		}
	}
}
