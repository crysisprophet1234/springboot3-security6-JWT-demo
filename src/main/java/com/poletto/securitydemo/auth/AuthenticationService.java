package com.poletto.securitydemo.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poletto.securitydemo.config.JwtService;
import com.poletto.securitydemo.entities.Role;
import com.poletto.securitydemo.entities.User;
import com.poletto.securitydemo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		
		var user = User.builder()
						.firstname(request.getFirstname())
						.lastname(request.getLastname())
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						.role(Role.USER)
						.build();
		
		repository.save(user);
		
		var jwtToken = jwtService.generateToken(user);
		
		return new AuthenticationResponse(jwtToken);
		
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
						)
				);
		
		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		
		var jwtToken = jwtService.generateToken(user);
		
		return new AuthenticationResponse(jwtToken);

	}
	
}
