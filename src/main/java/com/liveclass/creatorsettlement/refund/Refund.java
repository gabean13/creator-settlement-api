package com.liveclass.creatorsettlement.refund;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "refund",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_refund_idempotency_key", columnNames = "idempotency_key"),
      @UniqueConstraint(name = "uk_refund_purchase_id", columnNames = "purchase_id")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "purchase_id", nullable = false)
  private Long purchaseId;

  @Column(nullable = false)
  private Long refundAmount;

  @Column(nullable = false)
  private LocalDateTime refundedAt;

  @Column(name = "idempotency_key", nullable = false, length = 64)
  private String idempotencyKey;

  @Builder
  private Refund(
      Long purchaseId, Long refundAmount, LocalDateTime refundedAt, String idempotencyKey) {
    this.purchaseId = purchaseId;
    this.refundAmount = refundAmount;
    this.refundedAt = refundedAt;
    this.idempotencyKey = idempotencyKey;
  }
}
