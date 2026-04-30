package com.liveclass.creatorsettlement.purchase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    name = "purchase",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_purchase_idempotency_key", columnNames = "idempotency_key"),
      @UniqueConstraint(
          name = "uk_purchase_customer_course",
          columnNames = {"customer_id", "course_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "course_id", nullable = false)
  private Long courseId;

  @Column(name = "paid_amount", nullable = false)
  private Long paidAmount;

  @Column(name = "paid_at", nullable = false)
  private LocalDateTime paidAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private PurchaseStatus status;

  @Column(name = "idempotency_key", nullable = false, length = 64)
  private String idempotencyKey;

  @Builder
  private Purchase(
      Long customerId,
      Long courseId,
      Long paidAmount,
      LocalDateTime paidAt,
      PurchaseStatus status,
      String idempotencyKey) {
    this.customerId = customerId;
    this.courseId = courseId;
    this.paidAmount = paidAmount;
    this.paidAt = paidAt;
    this.status = status;
    this.idempotencyKey = idempotencyKey;
  }
}
