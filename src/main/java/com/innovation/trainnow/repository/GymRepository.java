package com.innovation.trainnow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innovation.trainnow.entity.Gym;

public interface GymRepository extends JpaRepository<Gym, Long> {

}
