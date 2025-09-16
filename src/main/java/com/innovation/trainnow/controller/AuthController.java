package com.innovation.trainnow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innovation.trainnow.dto.LoginRequestDto;
import com.innovation.trainnow.dto.LoginResponseDto;
import com.innovation.trainnow.dto.SignUpRequestDto;
import com.innovation.trainnow.dto.SignupResponseDto;
import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
	    return ResponseEntity.ok(authService.login(loginRequestDto));
	}

    @PostMapping("/signup/user")
    public ResponseEntity<Users> signup(@RequestBody SignUpRequestDto signupRequestDto) {
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }
}
