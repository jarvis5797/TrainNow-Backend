package com.innovation.trainnow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddGymRequestDto {
	
	private Long ownerId;
	
	private String name;
	
	private String city;
	
	private String description;
	
	private String address;

}
