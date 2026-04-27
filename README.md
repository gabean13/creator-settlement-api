# 크리에이터 정산 API

## 프로젝트 개요
> 라이브클래스 크리에이터의 정산 관련 API 개발

## 실행 방법
도커로 실행 방법 작성 예정

## 테스트 실행 방법
테스트를 위한 HTTP 파일 첨부 예정

---

## 기술 스택
- 서버: SpringBoot, Java, JPA
- DB: PostgreSQL, Redis

## 데이터 모델 설명
> ERD 추가 예정

- 유저
    - 아이디, 유형(고객, 크리에이터, 운영자)
- 강의
- 구매
- 환불
- 수수료
    - 시작 날짜, 종료 날짜,

---

## 요구사항 해석 및 가정
- 모든 일시는 KST 기준으로 저장/조회한다

### 인증/인가
- 운영자/고객/크리에이터 유형마다 접근 가능한 자원을 분리해야하지만, 해당 과제에서는 간단한 인증/인가를 구현하기위해 각 요청을 id와 type으로만 구분하고 필터에서 접근 가능한 자원과 식별을 최소한으로 처리한다.
    - 고객(customer) 접근 가능 자원: 판매 내역 관리 API
    - 크리에이터(creator) 접근 가능 자원: 강의 등록 API, 정산 금액 계산 API
    - 운영자(admin) 접근 가능 자원: 정산 내역 집계 API

### 정산 규칙
- 수수료율은 **결제 일시 기준**의 수수료를 적용
  - 결제 시점에 적용 수수료율을 스냅샷 저장해 수수료율이 변경되더라도 기존 거래의 정산 금액은 영향받지 않도록 함.
- 정산 금액 = 판매 금액 - 취소 금액 - 수수료
  - 취소 금액 > 정산 금액인 경우 음수 허용
- 정산 상태:
  - PENDING: 정산 대상 집계는 됐지만 아직 확정 전
  - CONFIRMED: 정산 금액 확정 (기준: 결제 후 14일 경과 시)
  - PAID: 실제 송금 완료
- 정산은 매달 25일 새벽 4시에 진행
  - 중복 정산을 막기 위해 분산 락과 정산 코드 상의 멱등성을 보장(ShedLock + DB)
  - 결제 후 7일 경과 시 개별 SaleRecord가 CONFIRMED
  - 25일 배치에서 CONFIRMED 상태인 것만 모아서 PAID

### 구매 규칙
- 중복 결제를 막기 위해 클라이언트에서 요청 시, 멱등키를 반드시 함께 보내고 서버에서는 멱등키 확인, DB Unique Key로 막는다
- 동일한 제품은 한 번만 구매 가능
- 구매 상태
  - PAID: 결제 완료
  - CANCELED: 환불

### 환불 규칙
- 중복 환불을 막기 위해 클라이언트에서 요청 시, 멱등키를 반드시 함께 보내고 서버에서는 멱등키 확인, DB Unique Key로 막는다
- 부분 환불 가능, 그 이후에 추가 환불 불가.
- 결제 후 14일 경과 시 환불 불가

## API 목록 및 예시
> API 명세서 URL , 실제 응답Body 추가 예정

**인증/인가 필터링**
1. RequestHeader에 `Authorization: {type} {id}`를 명시하지 않은 사용자인 경우, `401 Unauthorized` 응답
2. 접근할 수 없는 자원에 접근한 경우, `403 Forbidden` 응답

**리스트 조회 시 페이지네이션**
- 모든 조회 API는 오프셋 기반 페이지네이션(page, limit)을 사용한다. 선택 근거는 다음과 같다.
  - 정산,구매 내역은 특정 기간/월 단위로 범위가 제한된 조회이고, 사용자가 "마지막 페이지로 이동", "전체 건수 확인" 등 위치 기반 탐색을 할 가능성이 높기 때문이다.
- 페이지네이션 기본값은 사용자의 데이터 규모와 맥락에 따라 적용한다.
  - B2C 화면(구매 내역): 개인 단위 데이터량이 적어 기본 10건
  - B2B/운영 화면(정산 내역, 정산 집계): 데스크톱 기반 분석 용도이며 단위 데이터량이 많아 기본 50건

### 고객 API
**강의 구매 API**
- 설명: 고객이 새로운 강의를 구매합니다.
- URI: **POST /api/v1/courses/{courseId}/purchases** 
- RequestHeader: Authorization: Customer {id}
- RequestBody:
  - paidAmount: 구매 금액
  - paidDate: 구매 일시
- 응답 및 예외처리:
  - 201 Created: 정상적으로 구매가 완료된 경우.
  - 400 Bad Request: amount가 0원 미만인 경우 잘못된 요청으로 처리합니다.
  - 409 Conflict: 이미 구매한 이력이 있어 중복 구매가 불가능한 경우, 오류메세지와 함께 응답합니다.

**강의 환불 API**
- 설명: 고객이 구매한 강의를 환불합니다.
- URI: POST /api/v1/purchases/{purchaseId}/refunds
- RequestHeader: Authorization: Customer {id}
- RequestBody:
  - refundAmount: 취소 금액
- 응답 및 예외 처리:
  - 201 Created: 환불이 정상적으로 승인/처리된 경우 
  - 400 Bad Request: refundAmount가 0원 미만이거나 기존 판매 금액을 초과하는 경우, 또는 서버에서 확인한 환불 요청 날짜가 구매 날짜보다 과거인 경우, 또는 고객이 구매한 내역이 없는 경우 오류 메시지와 함께 응답합니다

**구매 내역 조회 API**
- 설명: 고객(me)의 구매 내역(결제 및 취소)을 조회합니다.
- URI: GET /api/v1/me/purchases
- RequestHeader: Authorization: Customer {id}
- Query Parameters: 필터링과 페이지네이션은 URL 쿼리 파라미터로 처리합니다 .
  - creatorId: 크리에이터 식별자 (선택)
  - startDate, endDate: 조회 기간 (기본 해당 월 시작 일부터 현재까지)
  - status: 상태 필터 (예: PURCHASE, REFUND) (선택)
  - limit: 페이지당 항목 수 (기본 10, 최대 50)
  - page: 페이지네이션 위치
- 응답 및 예외 처리:
  - 200 OK: 페이징 정보와 함께 구매 내역(강의 id, 강의 title, 크리에이터 id, 크리에이터 name, 금액, 취소인 경우 환불 금액, 상태)을 응답합니다.
    - 구매 내역이 없으면 빈 리스트를 응답합니다.
    - end_date가 미래 시점인 경우, 현재 날짜로 변경하여 응답합니다.
  - 400 BadRequest: startDate가 미래 시점인 경우, 오류 메세지와 함께 응답합니다.

### 크리에이터 API
**강의 등록 API**
- 설명: 크리에이터가 새로운 강의를 생성합니다.
- URI: POST /api/v1/courses
- RequestHeader: Authorization: Creator {id}
- 응답 및 예외 처리:
   - 201 Created: 생성된 강의 정보 반환.

**크리에이터 정산 금액 조회 API**
- 설명: 크리에이터 본인의 데이터 조회합니다.
- Method & URI: GET /api/v1/me/settlements
- RequestHeader: Authorization: Creator {id}
- Query Parameters: 필터링과 페이지네이션은 URL 쿼리 파라미터로 처리합니다.
  - yearMonth: 조회 연월 (2025-02)
  - limit: 페이지당 항목 수 (기본 50, 최대 100)
  - page: 페이지네이션 위치
- 응답 및 예외 처리:
  - 200 OK: 페이징 정보와 함께 정산 내역(총 순 판매 금액, 총 취소 금액, 총 수수료, 총 정산 예정 금액, 판매/취소 건수, 상세 목록: 총 강의 id, 강의 title, 구매 금액, 상태)을 응답합니다.
    - 취소 금액 > 정산 금액인 경우 음수로 응답합니다.
    - 내역이 없는 경우 빈 리스트를 응답합니다.
  - 400 BadRequest: yearMonth가 미래 시점인 경우, 오류 메세지와 함께 응답합니다.

### 운영자 API
**정산 내역 집계 API**
- 설명: 운영자가 특정 기간의 전체 정산 집계 결과를 조회합니다.
- Method & URI: GET /api/v1/settlements/summary
- RequestHeader: Authorization: Admin {id}
- Query Parameters: 필터링과 페이지네이션은 URL 쿼리 파라미터로 처리합니다.
   - startDate: 시작 날짜 (예: 2026-04-01T00:00:00)
   - endDate: 종료 날짜 (예: 2026-04-30T23:59:59)
- 응답 및 예외 처리:
   - 200 OK: 정산 내역(총 수수료, 총 판매 금액, 총 정산 금액, 총 취소 금액, 판매/취소 건수, 상세 목록: 크리에이터 id, 강의 id, 구매 금액, 상태)을 응답합니다.
     - 내역이 없는 경우 빈 리스트를 응답합니다.
   - 400 BadRequest: endDate가 미래 시점인 경우, 오류 메세지와 함께 응답합니다.

## 설계 결정과 이유
PostgreSQL 선택 이유
멱등키 선택 이유
ShedLock + DB 선택 이유
수수료 스냅샷 방법 선택 이유

## 미구현 / 제약사항
### 미구현
- 인증
- 실제 결제 API 연결

---

## AI 활용 범위
