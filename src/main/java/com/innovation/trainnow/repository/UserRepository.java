package com.innovation.trainnow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innovation.trainnow.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long>{

	Optional<Users> findByEmail(String email);

	Optional<Users> findByPhoneNumber(String phoneNumber);
	
	Optional<Users> findByEmailAndPhoneNumber(String email, String phoneNumber);

}
