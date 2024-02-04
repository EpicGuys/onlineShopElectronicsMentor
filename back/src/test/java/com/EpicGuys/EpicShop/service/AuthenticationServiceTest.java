package com.EpicGuys.EpicShop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EpicGuys.EpicShop.dto.AuthenticationRequest;
import com.EpicGuys.EpicShop.dto.RegisterRequest;
import com.EpicGuys.EpicShop.entity.User;
import com.EpicGuys.EpicShop.exceptions.AuthenticationException;
import com.EpicGuys.EpicShop.exceptions.RegistrationException;
import com.EpicGuys.EpicShop.jpa.AccessTokenRepository;
import com.EpicGuys.EpicShop.jpa.RefreshTokenRepository;
import com.EpicGuys.EpicShop.jpa.UserRepository;
import com.EpicGuys.EpicShop.security.JwtService;

import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private RefreshTokenRepository refreshTokenRepository;
	@Mock
	private AccessTokenRepository accessTokenRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtService jwtService;
	@Mock
	private AuthenticationManager authenticationManager;
	@InjectMocks
	private AuthenticationService authService;
	
	private static Optional<User> userWrapper;
	private static RegisterRequest registerRequest; 
	private static AuthenticationRequest authenticationRequest;
	private static String email = "maxik@mail.ru";
	private static String password = "123";
	
	@BeforeAll
	public static void setup() {
		userWrapper = mock(Optional.class);
		registerRequest = mock(RegisterRequest.class);
		registerRequest = RegisterRequest.builder()
				.email(email)
				.password(password)
				.build();	
		authenticationRequest = AuthenticationRequest.builder()
				.email(email)
				.password(password)
				.build();			
	}
	
	@Test
	public void Register_ShouldReturnUserWithThisEmailAlreadyExists() {
		when(userWrapper.isPresent()).thenReturn(true);
		when(userRepository.getUserByEmail(email)).thenReturn(userWrapper);
		
		Assertions.assertThrows(RegistrationException.class, () -> {
			authService.register(registerRequest);
		});
	}
	
	@Test
	public void Register_ShouldCallSaveUserOneTime() {
		when(userWrapper.isPresent()).thenReturn(false);
		when(passwordEncoder.encode(password)).thenReturn("hohohoho");
		when(userRepository.getUserByEmail(email)).thenReturn(userWrapper);
		
		try {
			authService.register(registerRequest); //this call will throw exception but method that we try to check is called before
		} catch (Exception exception) {
			verify(userRepository, times(1)).save(any());
		}
	}
	
	@Test
	public void Register_ShouldCallSaveRefereshSaveAccessTokenByOneTime() throws RegistrationException{
		when(userWrapper.isPresent()).thenReturn(false);
		when(passwordEncoder.encode(password)).thenReturn("hohohoho");
		when(userRepository.getUserByEmail(email)).thenReturn(userWrapper);
		when(refreshTokenRepository.save(any())).thenReturn(null);
		when(accessTokenRepository.save(any())).thenReturn(null);
		when(jwtService.extractExpirationTimeRefreshToken(any())).thenReturn(new Date());
		when(jwtService.extractExpirationTimeAccessToken(any())).thenReturn(new Date());
			
		authService.register(registerRequest); 
		
		verify(refreshTokenRepository, times(1)).save(any());
		verify(accessTokenRepository, times(1)).save(any());
	}
	
	@Test
	public void Authenticate_ShouldThrowAuthenticationException() {
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(null));
		
		Assertions.assertThrows(AuthenticationException.class, () -> {
			authService.authenticate(authenticationRequest);
		});
	}
	
	@Test
	public void Authenticate_ShouldCallGetUserByUserEmail() {
		when(authenticationManager.authenticate(any())).thenReturn(null);
		
		try{
			authService.authenticate(authenticationRequest);
		}
		catch(Exception ex) {
			verify(userRepository, times(1)).getUserByEmail(email);
		}	
	}
}
