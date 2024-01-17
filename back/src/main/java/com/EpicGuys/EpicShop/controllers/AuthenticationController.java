package com.EpicGuys.EpicShop.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.EpicGuys.EpicShop.dto.AuthenticationRequest;
import com.EpicGuys.EpicShop.dto.AuthenticationResponse;
import com.EpicGuys.EpicShop.dto.LogoutRequest;
import com.EpicGuys.EpicShop.dto.RegisterRequest;
import com.EpicGuys.EpicShop.dto.UpdateAccessTokenRequest;
import com.EpicGuys.EpicShop.exceptions.AuthenticationException;
import com.EpicGuys.EpicShop.exceptions.RegistrationException;
import com.EpicGuys.EpicShop.exceptions.TokenNotFoundException;
import com.EpicGuys.EpicShop.exceptions.TokenOutOfWhiteListException;
import com.EpicGuys.EpicShop.exceptions.TokenValidationException;
import com.EpicGuys.EpicShop.service.AuthenticationService;



@Controller
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<AuthenticationResponse> register (
			@RequestBody RegisterRequest request) throws RegistrationException {
		return ResponseEntity.ok().body(authService.register(request));
	}
	
	@PostMapping("/authentication")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<AuthenticationResponse> authenticate(
			@RequestBody AuthenticationRequest request) throws AuthenticationException{
		return ResponseEntity.ok().body(authService.authenticate(request));
	}
	
	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<AuthenticationResponse> refresh(
			@RequestBody UpdateAccessTokenRequest request) throws TokenValidationException, IllegalArgumentException, TokenOutOfWhiteListException{
		return ResponseEntity.ok().body(authService.refreshAccessToken(request));
	}
	
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> logout(@RequestBody LogoutRequest request) throws TokenValidationException, TokenNotFoundException{
		authService.logout(request);
		return ResponseEntity.ok().body("Logout successfully");
	}
	
	@GetMapping("/hello")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> register(){
		return ResponseEntity.ok("Hello!");
	}
}
