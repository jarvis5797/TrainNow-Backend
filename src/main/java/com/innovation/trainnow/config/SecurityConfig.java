package com.innovation.trainnow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.filter.JwtAuthFilter;
import com.innovation.trainnow.repository.UserRepository;
import com.innovation.trainnow.serviceImpl.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
    private final HandlerExceptionResolver handlerExceptionResolver;
	private final JwtAuthFilter jwtAuthFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
		http
	    .csrf(csrf -> csrf.disable())
	    .authorizeHttpRequests(auth -> auth
	        .requestMatchers("/api/v1/auth/**").permitAll()
	        .anyRequest().authenticated()
	    )
	    .oauth2Login(oauth2 -> oauth2
	        .successHandler(oAuth2SuccessHandler)
	        .failureHandler((request, response, exception) ->
	            handlerExceptionResolver.resolveException(request, response, null, exception))
	    )
	    .sessionManagement(session -> 
	        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // needed for OAuth2
	    )
	    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	       
	        .exceptionHandling(ex -> ex
	            .authenticationEntryPoint((request, response, authException) -> {
	                handlerExceptionResolver.resolveException(request, response, null, authException);
	            })
	            .accessDeniedHandler((request, response, accessDeniedException) -> {
	                handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
	            })
	        );

	    return http.build();
	}

	

}
