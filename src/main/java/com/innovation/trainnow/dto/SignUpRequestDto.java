package com.innovation.trainnow.dto;

import java.math.BigDecimal;
import java.util.List;

import com.innovation.trainnow.entity.Booking;
import com.innovation.trainnow.entity.Enum;
import com.innovation.trainnow.entity.Gym;
import com.innovation.trainnow.entity.Payment;
import com.innovation.trainnow.entity.WalletTransaction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    private String name;

    private String email;

    private String password;
    
    private String phoneNumber;
    
    private String role;  

}
