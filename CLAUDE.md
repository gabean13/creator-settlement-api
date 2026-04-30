# CLAUDE.md

## Project context
라이브클래스 크리에이터 정산 API.
- Spring Boot 4.0.6, Java 21
- Build: Gradle  
- DB: PostgreSQL(데이터 저장) + Spring Data JPA + Redis(동시성 제어)

## MUST NOT
- 테스트 허락 받지 않고 삭제
- 데이터베이스 쿼리 직접 날리기
- 환경변수를 코드에 하드코딩

## Common commands
```bash
#빌드
./gradlew build

#테스트: 
./gradlew test

#단일 테스트: 
./gradlew test --tests "ClassName.methodName"

#실행: 
./gradlew bootRun
```

## Additional Instructions
- Project overview: @README.md
- Git workflow: @docs/git-instructions.md

## Code Style
- Lombok 사용 (@RequiredArgsConstructor 선호, @Data/@Setter 지양)
- 생성자 주입만 사용 (필드 주입 금지)
- DTO는 record 사용
- Service는 인터페이스 없이 구현체만 (불필요한 추상화 지양)

## Architecture
- 패키지 구조: 도메인별 분리

## Testing
- 단위 테스트는 @ExtendWith(MockitoExtension.class)
- 통합 테스트는 @SpringBootTest + Testcontainers
- 테스트 메서드명: "should_기대결과_when_조건" 또는 한글
