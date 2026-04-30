package com.liveclass.creatorsettlement.settlement;

public enum SettlementStatus {
  // 정산 대상으로 집계됐지만 아직 확정 전
  PENDING,
  // 결제 후 7일 경과로 정산 금액 확정 (배치 대상)
  CONFIRMED,
  // 25일 정산 배치에서 송금 완료
  PAID
}
