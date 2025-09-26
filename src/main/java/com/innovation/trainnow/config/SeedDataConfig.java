package com.innovation.trainnow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.innovation.trainnow.entity.Enum.Role;
import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.repository.UserRepository;

@Component
public class SeedDataConfig implements CommandLineRunner {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void run(String... args) throws Exception {
		if(userRepository.count() ==0) {
			Users admin = new Users();
			admin.setName("Admin");
			admin.setEmail("trainnowadmin@gmail.com");
			admin.setPhoneNumber("1234567890");
			admin.setPassword(passwordEncoder.encode("1234567"));
			admin.setRole(Role.ADMIN);
			admin.setIsVerified(true);
			userRepository.save(admin);
		}
	}

}
