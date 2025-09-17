package com.innovation.trainnow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innovation.trainnow.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long>{

	Users findByEmail(String email);

	Users findByPhoneNumber(String phoneNumber);

}
