---
name: gh-commit
description: Conventional Commits 형식(한국어)으로 현재 변경사항을 커밋합니다. push는 하지 않습니다. 사용자가 커밋/commit을 만들고 싶다고 할 때 호출됩니다.
allowed-tools: Bash(git status*), Bash(git diff*), Bash(git log*), Bash(git add*), Bash(git commit*), Bash(./gradlew*)
---

# Git Commit

`$ARGUMENTS`에 사용자가 메시지나 의도를 전달했다면 우선 반영. 비어 있으면 변경사항을 분석해 자동 작성.

## 절차

### 1. 상태 확인 (병렬 실행)
- `git status` — 추적/미추적 파일 확인 (`-uall` 플래그 절대 금지)
- `git diff` — staged/unstaged 변경 모두
- `git log -5 --oneline` — 최근 커밋 스타일 참고

### 2. 사전 검증
- **변경 없음**: staged + unstaged 모두 비어 있으면 빈 커밋 만들지 말고 종료.

### 3. 빌드/테스트 (선택, 권장)
git-instructions.md는 커밋 전 `./gradlew build`, `./gradlew test` 통과를 요구한다. 사용자가 "검증 생략" 등을 명시하지 않으면 실행을 권장. 실패 시 커밋하지 않고 사용자에게 알림.

추가로 pre-commit 훅에서 `./gradlew spotlessCheck`가 자동 실행된다. **lint 실패로 커밋이 막히면 절대 `--no-verify`로 우회하지 말고**, 다음을 실행해 자동 수정 후 다시 staging:

```bash
./gradlew spotlessApply
```

수정된 파일을 다시 `git add` 한 뒤 커밋한다.

### 4. 메시지 작성
**형식**: Conventional Commits, 한국어
```
<type>(<scope>): <subject>
```
- **type**: `feat`, `fix`, `refactor`, `test`, `docs`, `chore` 중 하나
- **scope**: 패키지/도메인 (예: `auth`, `settlement`, `purchase`, `refund`, `course`, `user`). 변경이 단일 도메인에 국한되지 않으면 생략 가능
- **subject**: 한국어, 명령형/현재형, 50자 이내, "왜"보다 "무엇" 위주
- 본문(선택): 한 줄 띄우고 변경 이유/맥락. 줄당 72자 이내

**예시**:
- `feat(settlement): 수수료 스냅샷 저장 로직 추가`
- `fix(purchase): 멱등키 중복 검증 누락 수정`
- `refactor(course): Service 인터페이스 제거하고 구현체로 통합`

### 5. 스테이징 & 커밋
- `git add -A` 또는 `git add .` 사용 금지 — 의도하지 않은 파일 포함 위험. 변경된 파일을 명시적으로 나열해 add.
- HEREDOC으로 메시지 전달:

```bash
git commit -m "$(cat <<'EOF'
feat(settlement): 수수료 스냅샷 저장 로직 추가

결제 시점 수수료율을 기록해 추후 정책 변경에도
정산 금액이 영향받지 않도록 보장.
EOF
)"
```

### 6. 결과 확인
커밋 후 `git status`로 결과 확인하고 사용자에게 커밋 해시·요약 출력.

## 주의사항 (git-instructions.md 준수)
- **push는 절대 자동 실행하지 않는다.** 커밋만.
- **`--amend` 금지** (이미 푸시된 커밋이면 히스토리 변경). 새 커밋으로 만든다.
- **`--no-verify` 금지**. 훅 실패 시 원인을 고치고 다시 시도.
- `--force` 관련 동작 일체 금지.
- main/develop에 직접 커밋해야 하는 상황이면 사용자에게 먼저 확인.
- pre-commit 훅이 실패해 커밋이 만들어지지 않았다면, 절대 `--amend`로 우회하지 말고 원인 수정 후 새 커밋으로 시도.
