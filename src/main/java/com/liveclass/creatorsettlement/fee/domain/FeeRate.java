package com.liveclass.creatorsettlement.fee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeeRate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 0.1500 = 15%
  @Column(nullable = false, precision = 6, scale = 4)
  private BigDecimal rate;

  @Column(nullable = false)
  private LocalDateTime startAt;

  // null 이면 현재 적용 중
  @Column private LocalDateTime endAt;

  @Builder
  private FeeRate(BigDecimal rate, LocalDateTime startAt, LocalDateTime endAt) {
    this.rate = rate;
    this.startAt = startAt;
    this.endAt = endAt;
  }
}
