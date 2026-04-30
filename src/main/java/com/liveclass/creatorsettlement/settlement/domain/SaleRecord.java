package com.liveclass.creatorsettlement.settlement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "sale_record",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_sale_record_purchase_id", columnNames = "purchase_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "purchase_id", nullable = false)
  private Long purchaseId;

  @Column(name = "creator_id", nullable = false)
  private Long creatorId;

  // 판매 금액 (취소 전 원금)
  @Column(name = "gross_amount", nullable = false)
  private Long grossAmount;

  // 누적 환불 금액
  @Column(name = "refund_amount", nullable = false)
  private Long refundAmount;

  // 결제 시점 수수료율 스냅샷 (이후 FeeRate 변경에도 영향 없음)
  @Column(name = "fee_rate_snapshot", nullable = false, precision = 6, scale = 4)
  private BigDecimal feeRateSnapshot;

  // 스냅샷 기준으로 산정된 수수료
  @Column(name = "fee_amount", nullable = false)
  private Long feeAmount;

  // 정산 금액 = grossAmount - refundAmount - feeAmount (음수 허용)
  @Column(name = "net_amount", nullable = false)
  private Long netAmount;

  @Column(name = "paid_at", nullable = false)
  private LocalDateTime paidAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private SettlementStatus status;

  @Builder
  private SaleRecord(
      Long purchaseId,
      Long creatorId,
      Long grossAmount,
      Long refundAmount,
      BigDecimal feeRateSnapshot,
      Long feeAmount,
      Long netAmount,
      LocalDateTime paidAt,
      SettlementStatus status) {
    this.purchaseId = purchaseId;
    this.creatorId = creatorId;
    this.grossAmount = grossAmount;
    this.refundAmount = refundAmount;
    this.feeRateSnapshot = feeRateSnapshot;
    this.feeAmount = feeAmount;
    this.netAmount = netAmount;
    this.paidAt = paidAt;
    this.status = status;
  }
}
