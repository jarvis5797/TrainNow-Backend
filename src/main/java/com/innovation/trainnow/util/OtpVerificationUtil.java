package com.innovation.trainnow.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.exception.UserNotFoundException;
import com.innovation.trainnow.repository.UserRepository;
import com.nimbusds.jose.util.StandardCharset;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
public class OtpVerificationUtil {

	@Value("${twilio.account.sid}")
	private String accountSid;

	@Value("${twilio.auth.token}")
	private String authToken;

	@Value("${twilio.whatsapp.from}")
	private String fromNumber;

	@Autowired
	private UserRepository userRepository;
	
	
	private static final SecureRandom secureRandom = new SecureRandom();

	private void sendOtpToWhatsapp(String toNumber, String otp) {
		Twilio.init(accountSid, authToken);

		String body = "Your OTP to verify for TrainNow is " + otp + " It will expire in 5 minute";

		Message.creator(new PhoneNumber("whatsapp:+91" + toNumber), new PhoneNumber(fromNumber), body).create();

	}

	public String send(String phoneNumber, String email) {
		
		Users user =  userRepository.findByEmailAndPhoneNumber(email, phoneNumber)
			.orElseThrow(()-> new UserNotFoundException("No user found with email :"+email
					+" and phone number: "+phoneNumber));
		String phoneOtp = generateOtp();
		String emailOtp = generateOtp();
		hashAndSaveOtp(phoneOtp, emailOtp, user);
		sendOtpToWhatsapp(phoneNumber, phoneOtp);
		return "Otp is sent to email and phone";
		
	}
	
	private static String generateOtp() {
		int otp = 100000 + secureRandom.nextInt(900000);
		return String.valueOf(otp);
	}
	
	private void hashAndSaveOtp(String phoneOtp , String emailOtp, Users user) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] phoneHash = digest.digest(phoneOtp.getBytes(StandardCharsets.UTF_8));
			String hashedPhoneOtp = Base64.getEncoder().encodeToString(phoneHash);
			byte[] emailHash = digest.digest(phoneOtp.getBytes(StandardCharsets.UTF_8));
			String hashedEmailOtp = Base64.getEncoder().encodeToString(emailHash);
			user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
			user.setPhoneOtp(hashedPhoneOtp);
			user.setEmailOtp(hashedEmailOtp);
			userRepository.save(user);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error hashing OTP", e);
		}
		
		
	}
	
	

}
