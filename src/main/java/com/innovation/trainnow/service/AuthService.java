package com.innovation.trainnow.service;

import com.innovation.trainnow.dto.LoginRequestDto;
import com.innovation.trainnow.dto.LoginResponseDto;
import com.innovation.trainnow.dto.SignUpRequestDto;
import com.innovation.trainnow.entity.Users;

public interface AuthService {

	Users signup(SignUpRequestDto signupRequestDto);

	LoginResponseDto login(LoginRequestDto loginRequestDto);

}
