package com.EpicGuys.EpicShop.service;

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
import com.EpicGuys.EpicShop.dto.RegisterRequest;
import com.EpicGuys.EpicShop.dto.UpdateAccessTokenRequest;
import com.EpicGuys.EpicShop.entity.Role;
import com.EpicGuys.EpicShop.entity.User;
import com.EpicGuys.EpicShop.exceptions.RegistrationException;
import com.EpicGuys.EpicShop.exceptions.TokenValidationException;
import com.EpicGuys.EpicShop.jpa.UserRepository;
import com.EpicGuys.EpicShop.security.JwtService;
import com.EpicGuys.EpicShop.security.UserDetailsImpl;

@Service
public class AuthenticationService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) throws RegistrationException{
		if(userRepository.getUserByEmail(request.getEmail()).isPresent()){
			throw new RegistrationException("User with this email address already exists");
		}
		User user = User.builder()
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						//.roles(Set.of(Role.USER))
						.build();
		userRepository.save(user);
		String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
		String refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
		return AuthenticationResponse.builder()
									.accessToken(accessToken)
									.refreshToken(refreshToken)
									.build();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) throws BadCredentialsException{
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			User user = userRepository.getUserByEmail(request.getEmail()).get();
			String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
			String refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
			return AuthenticationResponse.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
	}
	
	public AuthenticationResponse refreshAccessToken(UpdateAccessTokenRequest request) throws TokenValidationException {
		if(!jwtService.isRefreshTokenValid(request.getRefreshToken())) {
			//TODO logging
			System.out.println("Refresh token is not valid");
			throw new TokenValidationException("Refresh token is not valid");
			
		}
		String username = jwtService.extractUserEmailRefreshToken(request.getRefreshToken());
		User user = userRepository.getUserByEmail(username).get();
		String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
		String refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
		return AuthenticationResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}
}
