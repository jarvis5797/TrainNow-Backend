package com.innovation.trainnow.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.innovation.trainnow.dto.LoginRequestDto;
import com.innovation.trainnow.dto.LoginResponseDto;
import com.innovation.trainnow.dto.SignUpRequestDto;
import com.innovation.trainnow.entity.Enum;
import com.innovation.trainnow.entity.Enum.Role;
import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.exception.UserNotFoundException;
import com.innovation.trainnow.filter.JwtAuthFilter;
import com.innovation.trainnow.filter.JwtService;
import com.innovation.trainnow.repository.UserRepository;
import com.innovation.trainnow.service.AuthService;

import jakarta.transaction.Transactional;

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
		if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists");
		}
		user.setEmail(signupRequestDto.getEmail());
		user.setPassword(encoder.encode(signupRequestDto.getPassword()));
		if (userRepository.findByPhoneNumber(signupRequestDto.getPhoneNumber()).isPresent()) {
			throw new RuntimeException("Phone number already exists");
		}
		user.setPhoneNumber(signupRequestDto.getPhoneNumber());
		if (signupRequestDto.getRole().equals("user")) {
			user.setIsVerified(false);
			user.setRole(Role.USER);
		}
		user.setProviderType(Enum.ProviderType.MANUAL);
		return userRepository.save(user);
	}

	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
	    try {
	        Users authUser;
	        String input = loginRequestDto.getIdentifier(); // Can be email or phone number

	        if (input.contains("@")) {
	            authUser = userRepository.findByEmail(input)
	            		.orElseThrow(()-> new UserNotFoundException("No user found with email: " + input));
	        } else {
	            authUser = userRepository.findByPhoneNumber(input)
	            		.orElseThrow(()-> new UserNotFoundException("No user found with phoneNumber: " + input));
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
	@Transactional
//	http://localhost:8080/oauth2/authorization/google
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        Enum.ProviderType providerType = jwtService.getProviderTypeFromRegistrationId(registrationId);
        String providerId = jwtService.determineProviderIdFromOAuth2User(oAuth2User, registrationId);
        Users user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Users emailUser = userRepository.findByEmail(email).orElse(null);

        if(user == null && emailUser == null) {
            Users newUser = new Users();
            newUser.setName(name != null ? name : "No Name");
            newUser.setEmail(email);
            newUser.setProviderId(providerId);
            newUser.setProviderType(providerType);
			newUser.setRole(Role.USER);
			newUser.setIsVerified(true);
			user = userRepository.save(newUser);
        } else if(user != null) {
            if(email != null && !email.isBlank() && !email.equals(user.getEmail())) {
                user.setEmail(email);
                userRepository.save(user);
            }
        } else if(emailUser.getEmail().equals(email) && emailUser.getProviderType().equals(Enum.ProviderType.MANUAL)) {
        	user = emailUser;
        	
        }
        else {
            throw new BadCredentialsException("This email is already registered with provider "+emailUser.getProviderType());
        }
        LoginResponseDto loginResponseDto = new LoginResponseDto(jwtService.generateAccessToken(user), user.getUserId());
        return ResponseEntity.ok(loginResponseDto);
    }


}
