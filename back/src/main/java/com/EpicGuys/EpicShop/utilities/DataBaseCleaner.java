package com.EpicGuys.EpicShop.utilities;

import java.util.Date;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger logger = Logger.getLogger(DataBaseCleaner.class.getName());
	
	@Async
	@Scheduled(fixedRate = 90000L)
	@Transactional
	public void accessTokenCleaner() {
		Integer count = accessTokenRepository.deleteAllByExpirationTimeBefore(new Date(System.currentTimeMillis()).getTime());
		logger.info("Expired access tokens(" + "amount - " + count + ") have been removed from data base");
	}
	
	@Async
	@Scheduled(fixedRate = 180000L)
	@Transactional
	public void refreshTokenCleaner() {
		Integer count = refreshTokenRepository.deleteAllByExpirationTimeBefore(new Date(System.currentTimeMillis()).getTime());
		logger.info("Expired refresh tokens(" + "amount - " + count + ") have been removed from data base");
	}
}
