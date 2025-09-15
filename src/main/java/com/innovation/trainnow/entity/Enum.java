package com.innovation.trainnow.entity;

public class Enum {
	public enum Role {
	    USER, GYM_OWNER, ADMIN
	}

	public enum BookingStatus {
	    BOOKED, CANCELLED
	}

	public enum TransactionType {
	    CREDIT, DEBIT
	}

	public enum PaymentMethod {
	    WALLET, RAZORPAY, STRIPE
	}

	public enum PaymentStatus {
	    SUCCESS, FAILED, PENDING
	}

}
