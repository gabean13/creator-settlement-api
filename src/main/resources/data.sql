-- 초기 시드 데이터 (PostgreSQL 전용)
-- 매 부팅마다 idempotent 하게 실행됨 (ON CONFLICT DO NOTHING)
-- 실행 조건: application.properties 의 spring.sql.init.mode=always
-- 테스트 환경(H2)에서는 spring.sql.init.mode=never 로 비활성화

-- 크리에이터
INSERT INTO users (id, name, type) VALUES
  (1, '김강사', 'CREATOR'),
  (2, '이강사', 'CREATOR'),
  (3, '박강사', 'CREATOR')
ON CONFLICT (id) DO NOTHING;

-- 학생 (고객)
INSERT INTO users (id, name, type) VALUES
  (11, 'student-1', 'CUSTOMER'),
  (12, 'student-2', 'CUSTOMER'),
  (13, 'student-3', 'CUSTOMER'),
  (14, 'student-4', 'CUSTOMER'),
  (15, 'student-5', 'CUSTOMER'),
  (16, 'student-6', 'CUSTOMER'),
  (17, 'student-7', 'CUSTOMER')
ON CONFLICT (id) DO NOTHING;

-- 강의
INSERT INTO course (id, creator_id, title, price, created_at) VALUES
  (1, 1, 'Spring Boot 입문', 50000, '2025-01-01 00:00:00'),
  (2, 1, 'JPA 실전',         80000, '2025-01-01 00:00:00'),
  (3, 2, 'Kotlin 기초',       60000, '2025-01-01 00:00:00'),
  (4, 3, 'MSA 설계',         120000, '2025-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

-- 수수료율 (20%, 무기한)
INSERT INTO fee_rate (id, rate, start_at, end_at) VALUES
  (1, 0.2000, '2024-01-01 00:00:00', NULL)
ON CONFLICT (id) DO NOTHING;

-- 결제 내역
-- 환불이 발생한 결제(3,4,5)는 status='CANCELED'
INSERT INTO purchase (id, customer_id, course_id, paid_amount, paid_at, status, idempotency_key) VALUES
  (1, 11, 1,  50000, '2025-03-05 10:00:00', 'PAID',     'seed-purchase-1'),
  (2, 12, 1,  50000, '2025-03-15 14:30:00', 'PAID',     'seed-purchase-2'),
  (3, 13, 2,  80000, '2025-03-20 09:00:00', 'CANCELED', 'seed-purchase-3'),
  (4, 14, 2,  80000, '2025-03-22 11:00:00', 'CANCELED', 'seed-purchase-4'),
  (5, 15, 3,   6000, '2025-01-31 23:30:00', 'CANCELED', 'seed-purchase-5'),
  (6, 16, 3,  60000, '2025-03-10 16:00:00', 'PAID',     'seed-purchase-6'),
  (7, 17, 4, 120000, '2025-02-14 10:00:00', 'PAID',     'seed-purchase-7')
ON CONFLICT (id) DO NOTHING;

-- 환불 내역
--  refund-1: sale-3 전액환불 (80,000)               → creator-1 / 2025-03 정산
--  refund-2: sale-4 부분환불 (30,000)               → creator-1 / 2025-03 정산
--  refund-3: sale-5 전액환불 ( 6,000), 월 경계 케이스 (결제 1월 / 취소 2월)
INSERT INTO refund (id, purchase_id, refund_amount, refunded_at, idempotency_key) VALUES
  (1, 3, 80000, '2025-03-25 10:00:00', 'seed-refund-1'),
  (2, 4, 30000, '2025-03-28 14:00:00', 'seed-refund-2'),
  (3, 5,  6000, '2025-02-01 09:00:00', 'seed-refund-3')
ON CONFLICT (id) DO NOTHING;

-- IDENTITY 시퀀스 보정: 명시적 ID 삽입 후 다음 자동 생성 ID 가 충돌하지 않도록
SELECT setval(pg_get_serial_sequence('users',     'id'), COALESCE((SELECT MAX(id) FROM users),     1));
SELECT setval(pg_get_serial_sequence('course',    'id'), COALESCE((SELECT MAX(id) FROM course),    1));
SELECT setval(pg_get_serial_sequence('fee_rate',  'id'), COALESCE((SELECT MAX(id) FROM fee_rate),  1));
SELECT setval(pg_get_serial_sequence('purchase',  'id'), COALESCE((SELECT MAX(id) FROM purchase),  1));
SELECT setval(pg_get_serial_sequence('refund',    'id'), COALESCE((SELECT MAX(id) FROM refund),    1));
