package com.innovation.trainnow.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.innovation.trainnow.entity.Users;
import com.innovation.trainnow.exception.UserNotFoundException;
import com.innovation.trainnow.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	
	private static final SecureRandom secureRandom = new SecureRandom();
	
	public String send(Long userId) {
		
		Users user =  userRepository.findById(userId)
			.orElseThrow(()-> new UserNotFoundException("User not found"));
		String phoneOtp = generateOtp();
		String emailOtp = generateOtp();
		hashAndSaveOtp(phoneOtp, emailOtp, user);
		try {
		sendOtpToWhatsapp(user.getPhoneNumber(), phoneOtp);
		sendOtpToEmail(user.getEmail(), emailOtp);
		return "Otp is sent to email and phone";
		}catch (ApiException e) {
			userRepository.delete(user);
			throw new RuntimeException(e);
		}
	}
	
	private void sendOtpToEmail(String email, String otp) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setSubject("Otp Verification for TrainNow");
			helper.setText("Use this verification code to verify your email for TrainNow: "+otp);
			helper.setTo(email);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Error sending mail", e);
		}
	}

	private void sendOtpToWhatsapp(String toNumber, String otp) {
		Twilio.init(accountSid, authToken);

		String body = "Your OTP to verify for TrainNow is " + otp + " It will expire in 5 minute";

		Message.creator(new PhoneNumber("whatsapp:+91" + toNumber), new PhoneNumber(fromNumber), body).create();

	}
	
	private static String generateOtp() {
		int otp = 100000 + secureRandom.nextInt(900000);
		return String.valueOf(otp);
	}
	
	private void hashAndSaveOtp(String phoneOtp , String emailOtp, Users user) {
			String hashedPhoneOtp = hasher(phoneOtp);
			String hashedEmailOtp = hasher(emailOtp);
			user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
			user.setPhoneOtp(hashedPhoneOtp);
			user.setEmailOtp(hashedEmailOtp);
			userRepository.save(user);
	}
	
	private String hasher(String otp) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hashByte = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
			String hashedOtp = Base64.getEncoder().encodeToString(hashByte);
			return hashedOtp;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error hashing OTP", e);
		}	
	}
	
	public String verifyOtp(Long userId, String emailOtp, String phoneOtp) {
		Users user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));
		String hashedPhoneOtp = hasher(phoneOtp);
		String hashedEmailOtp = hasher(emailOtp);
		if(hashedPhoneOtp.equals(user.getPhoneOtp()) && hashedEmailOtp.equals(user.getEmailOtp())) {
			user.setIsVerified(true);
			userRepository.save(user);
			return "Otp verified";
		}else {
			throw new RuntimeException("Otp not verified");
		}
		
		
	}
	
	

}
