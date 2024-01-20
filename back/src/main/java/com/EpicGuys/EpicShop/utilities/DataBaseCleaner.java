package com.EpicGuys.EpicShop.utilities;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.EpicGuys.EpicShop.jpa.AccessTokenRepository;
import com.EpicGuys.EpicShop.jpa.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class DataBaseCleaner{
	
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Async
	@Scheduled(fixedRate = 90000L)
	@Transactional
	public void accessTokenCleaner() {
		accessTokenRepository.deleteAllByExpirationTimeBefore(new Date(System.currentTimeMillis()).getTime());
		//TODO logginig
		System.out.println("accessTokenCleaner: " + new Date().toGMTString());
	}
	
	@Async
	@Scheduled(fixedRate = 180000L)
	@Transactional
	public void refreshTokenCleaner() {
		refreshTokenRepository.deleteAllByExpirationTimeBefore(new Date(System.currentTimeMillis()).getTime());
		//TODO logginig
		System.out.println("refreshTokenCleaner: " + new Date().toGMTString());
	}
}
