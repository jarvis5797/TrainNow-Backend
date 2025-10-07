package com.innovation.trainnow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innovation.trainnow.dto.AddGymRequestDto;
import com.innovation.trainnow.service.GymService;

@RestController
@RequestMapping("api/v1/gym")
public class GymController {

	@Autowired
	private GymService gymService;
	
//	@GetMapping("/getByCity/{city}")
//	public Page<T>
	
	@PostMapping("/addGym")
	public ResponseEntity<String> addGym(@RequestBody AddGymRequestDto gymRequestDto){
		return ResponseEntity.ok(gymService.addGym(gymRequestDto));
	}
}
