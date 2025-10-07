package com.innovation.trainnow.serviceImpl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.innovation.trainnow.dto.AddGymRequestDto;
import com.innovation.trainnow.entity.Gym;
import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.repository.GymRepository;
import com.innovation.trainnow.repository.UserRepository;
import com.innovation.trainnow.service.GymService;

@Service
public class GymServiceImpl implements GymService {
	
	private GymRepository gymRepository;
	
	private UserRepository userRepository;

	@Override
	public String addGym(AddGymRequestDto gymRequestDto) {
		Users owner = userRepository.findById(gymRequestDto.getOwnerId()).orElseThrow(()-> new UsernameNotFoundException("Gym owner not found")); 
		try {
			Gym newGym = new Gym();
			newGym.setOwner(owner);
			newGym.setName(gymRequestDto.getName());
			newGym.setCity(gymRequestDto.getCity());
			newGym.setDescription(gymRequestDto.getDescription());
			newGym.setAddress(gymRequestDto.getAddress());
			gymRepository.save(newGym);
			owner.setIsVerified(true);
			userRepository.save(owner);
			return "Gym Added Successfully!!";
		} catch (Exception e) {
			throw new UsernameNotFoundException("Error while adding gym");
		}
	}

}
