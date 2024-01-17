package com.EpicGuys.EpicShop.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
	
	private final static String ACCESS_SECRET_KEY = "zL1HB3Pch05Avfynovxrf/kpF9O2m4NCWKJUjEp27s9J2jEG3ifiKCGylaZ8fDeoONSTJP/wAzKawB8F9rOMNg==";
	private final static String REFRESH_SECRET_KEY = "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==";
	private final static Long ACCESS_TOKE_LIFE_TIME = 600000L;
	private final static Long REFRESH_TOKE_LIFE_TIME = 180000L;
	
	public String generateAccessToken(UserDetails userDetails) {
		return Jwts
				.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKE_LIFE_TIME))
				.signWith(getSignInKey(ACCESS_SECRET_KEY), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public String generateRefreshToken(UserDetails userDetails) {
		return Jwts
				.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKE_LIFE_TIME))
				.signWith(getSignInKey(REFRESH_SECRET_KEY), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean isAccessTokenValid(String token) {
		return isTokenValid(token, getSignInKey(ACCESS_SECRET_KEY));
	}
	
	public boolean isRefreshTokenValid(String token) {
		return isTokenValid(token, getSignInKey(REFRESH_SECRET_KEY));
	}
	
	private boolean isTokenValid(String token, Key key) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
			return true;
		}
		catch(RuntimeException  exception) {
			return false;
		}
	}
	
	public String extractUserEmailAccessToken(String token) {
		return extractClaim(token, getSignInKey(ACCESS_SECRET_KEY), Claims::getSubject);
	}
	
	public String extractUserEmailRefreshToken(String token) {
		return extractClaim(token, getSignInKey(REFRESH_SECRET_KEY), Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Key key, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token, key);
		if (claims != null) {
			return claimsResolver.apply(claims);
		}
		return null;
	}
	
	private Claims extractAllClaims(String token, Key key) {
		try {
			Claims claims = Jwts
					.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
			return claims;
		}
		catch(RuntimeException  exception) {
			return null;
		}
	}
	
	private Key getSignInKey(String secret) {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
