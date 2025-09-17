package com.innovation.trainnow.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.innovation.trainnow.dto.LoginRequestDto;
import com.innovation.trainnow.dto.LoginResponseDto;
import com.innovation.trainnow.dto.SignUpRequestDto;
import com.innovation.trainnow.entity.Enum.Role;
import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.filter.JwtAuthFilter;
import com.innovation.trainnow.filter.JwtService;
import com.innovation.trainnow.repository.UserRepository;
import com.innovation.trainnow.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Override
	public Users signup(SignUpRequestDto signupRequestDto) {
		Users user = new Users();
		user.setName(signupRequestDto.getName());
		if (userRepository.findByEmail(signupRequestDto.getEmail()) != null) {
			throw new RuntimeException("Email already exists");
		}
		user.setEmail(signupRequestDto.getEmail());
		user.setPassword(encoder.encode(signupRequestDto.getPassword()));
		if (userRepository.findByPhoneNumber(signupRequestDto.getPhoneNumber()) != null) {
			throw new RuntimeException("Phone number already exists");
		}
		user.setPhoneNumber(signupRequestDto.getPhoneNumber());
		if (signupRequestDto.getRole().equals("user")) {
			user.setRole(Role.USER);
		}
		return userRepository.save(user);
	}

	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
	    try {
	        Users authUser;
	        String input = loginRequestDto.getIdentifier(); // Can be email or phone number

	        if (input.contains("@")) {
	            authUser = userRepository.findByEmail(input);
	        } else {
	            authUser = userRepository.findByPhoneNumber(input);
	        }

	        if (authUser == null) {
	            throw new RuntimeException("Invalid email or phone number");
	        }

	        authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                authUser.getEmail(), 
	                loginRequestDto.getPassword()
	            )
	        );

	        String token = jwtService.generateAccessToken(authUser);
	        return new LoginResponseDto(token, authUser.getUserId());

	    } catch (BadCredentialsException ex) {
	        throw new RuntimeException("Invalid credentials");
	    }
	}


}
