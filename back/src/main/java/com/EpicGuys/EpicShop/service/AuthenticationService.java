package com.EpicGuys.EpicShop.service;

import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.EpicGuys.EpicShop.dto.AuthenticationRequest;
import com.EpicGuys.EpicShop.dto.AuthenticationResponse;
import com.EpicGuys.EpicShop.dto.LogoutRequest;
import com.EpicGuys.EpicShop.dto.RegisterRequest;
import com.EpicGuys.EpicShop.dto.UpdateAccessTokenRequest;
import com.EpicGuys.EpicShop.entity.AccessToken;
import com.EpicGuys.EpicShop.entity.RefreshToken;
import com.EpicGuys.EpicShop.entity.Role;
import com.EpicGuys.EpicShop.entity.User;
import com.EpicGuys.EpicShop.exceptions.AuthenticationException;
import com.EpicGuys.EpicShop.exceptions.RegistrationException;
import com.EpicGuys.EpicShop.exceptions.TokenNotFoundException;
import com.EpicGuys.EpicShop.exceptions.TokenOutOfWhiteListException;
import com.EpicGuys.EpicShop.exceptions.TokenValidationException;
import com.EpicGuys.EpicShop.jpa.AccessTokenRepository;
import com.EpicGuys.EpicShop.jpa.RefreshTokenRepository;
import com.EpicGuys.EpicShop.jpa.UserRepository;
import com.EpicGuys.EpicShop.security.JwtService;
import com.EpicGuys.EpicShop.security.UserDetailsImpl;

@Service
public class AuthenticationService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	private void saveTokens(String email, String accessToken, String refreshToken) {
		refreshTokenRepository.save(RefreshToken.builder()
				.email(email)
				.refreshToken(refreshToken)
				.build());
		accessTokenRepository.save(AccessToken.builder()
				.email(email)
				.accessToken(accessToken)
				.build());
	}
	
	public AuthenticationResponse register(RegisterRequest request) throws RegistrationException{
		if(userRepository.getUserByEmail(request.getEmail()).isPresent()){
			//TODO logging
			System.out.println("INFO: User with this email address already exists");
			throw new RegistrationException("User with this email address already exists");
		}
		User user = User.builder()
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						.roles(Set.of(Role.USER))
						.build();
		userRepository.save(user);
		String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
		String refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
		saveTokens(request.getEmail(), accessToken, refreshToken);
		return AuthenticationResponse.builder()
									.accessToken(accessToken)
									.refreshToken(refreshToken)
									.build();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException{
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		}
		catch(BadCredentialsException exception) {
			//TODO logging
			System.out.println("INFO: Email address or password is not correct");
			throw new AuthenticationException("Email address or password is not correct");
		}
		User user = userRepository.getUserByEmail(request.getEmail()).get();
		String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
		String refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
		saveTokens(request.getEmail(), accessToken, refreshToken);
		return AuthenticationResponse.builder()
									.accessToken(accessToken)
									.refreshToken(refreshToken)
									.build();
	}
	
	public AuthenticationResponse refreshAccessToken(UpdateAccessTokenRequest request) throws TokenValidationException, IllegalArgumentException, TokenOutOfWhiteListException {
		if(!jwtService.isRefreshTokenValid(request.getRefreshToken())) {
			//TODO logging
			System.out.println("INFO: Refresh token is not valid");
			throw new TokenValidationException("Refresh token is not valid");	
		}
		String username = jwtService.extractUserEmailRefreshToken(request.getRefreshToken());
		Optional<RefreshToken> token = refreshTokenRepository.findById(username);
		if(token.isEmpty() || !request.getRefreshToken().equals(token.get().getRefreshToken())) {
			System.out.println("INFO: There is no token in token while list");
			throw new TokenOutOfWhiteListException("There is no token in token while list");
		}
		User user = userRepository.getUserByEmail(username).get();
		String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
		String refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
		saveTokens(username, accessToken, refreshToken);
		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}
	
	public void logout(LogoutRequest request) throws TokenValidationException, TokenNotFoundException{
		if(!jwtService.isRefreshTokenValid(request.getRefreshToken())) {
			//TODO logging
			System.out.println("INFO: Refresh token is not valid");
			throw new TokenValidationException("Refresh token is not valid");
		}
		String username = jwtService.extractUserEmailRefreshToken(request.getRefreshToken());
		Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(username);
		if(refreshToken.isEmpty()) {
			System.out.println("INFO: There is no refresh token in the white list of refresh tokens");
			throw new TokenNotFoundException("There is no refresh token in the white list of refresh tokens");
		}
		refreshTokenRepository.deleteById(username);
		accessTokenRepository.deleteById(username);
		//refreshTokenRepository.deleteByEmail(jwtService.extractUserEmailRefreshToken(request.getRefreshToken()));
	}
}
