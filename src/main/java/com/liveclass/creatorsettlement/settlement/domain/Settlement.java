package com.liveclass.creatorsettlement.settlement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "settlement",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_settlement_creator_year_month",
            columnNames = {"creator_id", "year_month"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "creator_id", nullable = false)
  private Long creatorId;

  @Convert(converter = YearMonthAttributeConverter.class)
  @Column(name = "year_month", nullable = false, length = 7, columnDefinition = "VARCHAR(7)")
  private YearMonth yearMonth;

  @Column(name = "total_gross_amount", nullable = false)
  private Long totalGrossAmount;

  @Column(name = "total_refund_amount", nullable = false)
  private Long totalRefundAmount;

  @Column(name = "total_fee_amount", nullable = false)
  private Long totalFeeAmount;

  // 음수 허용 (취소 금액 > 정산 금액 케이스)
  @Column(name = "total_net_amount", nullable = false)
  private Long totalNetAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private SettlementStatus status;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @Builder
  private Settlement(
      Long creatorId,
      YearMonth yearMonth,
      Long totalGrossAmount,
      Long totalRefundAmount,
      Long totalFeeAmount,
      Long totalNetAmount,
      SettlementStatus status,
      LocalDateTime paidAt) {
    this.creatorId = creatorId;
    this.yearMonth = yearMonth;
    this.totalGrossAmount = totalGrossAmount;
    this.totalRefundAmount = totalRefundAmount;
    this.totalFeeAmount = totalFeeAmount;
    this.totalNetAmount = totalNetAmount;
    this.status = status;
    this.paidAt = paidAt;
  }
}
