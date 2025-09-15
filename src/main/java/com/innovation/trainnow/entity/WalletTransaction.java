package com.innovation.trainnow.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "wallet_transaction")
@Getter
@Setter
@RequiredArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Enum.TransactionType type;

    private LocalDateTime createdAt = LocalDateTime.now();
}

