---
name: gh-pr
description: 현재 브랜치의 변경사항으로 .github/PULL_REQUEST_TEMPLATE.md 기반의 PR을 생성합니다. 사용자가 PR/pull request를 만들고 싶다고 할 때 호출됩니다.
allowed-tools: Bash(gh pr *), Bash(gh repo *), Bash(git status*), Bash(git diff*), Bash(git log*), Bash(git branch*), Bash(git push*), Read
---

# GitHub Pull Request 생성

`$ARGUMENTS`에 사용자가 전달한 PR 메시지/관련 이슈 번호 등을 사용한다.

## 사전 점검

### 1. 브랜치 검증
- 현재 브랜치 확인: `git branch --show-current`
- **`main` 또는 `develop` 직접 PR 금지** (git-instructions.md 규칙). 해당 브랜치면 즉시 중단하고 사용자에게 알린다.
- 브랜치 네이밍 컨벤션: `<type>/<issue-number>-<description>` (feature, bugfix, hotfix, refactor, chore, test). 어긋나면 경고.

### 2. 변경사항 확인
다음을 병렬로 실행:
- `git status`
- `git diff origin/main...HEAD` (또는 `develop` — 기본 베이스 확인)
- `git log origin/main..HEAD --oneline`

커밋이 0개면 PR 생성 중단.

### 3. 빌드/테스트 권장
사용자에게 `./gradlew build`, `./gradlew test` 통과 여부를 확인. 실행하지 않았으면 실행을 권장하되, 사용자가 "그냥 진행"하라고 하면 진행.
- `./gradlew build` 안에서 `spotlessCheck`도 함께 실행된다. lint 실패 시 `./gradlew spotlessApply`로 자동 수정 후 다시 커밋·푸시.

### 4. 푸시 상태 확인
- 원격 추적 브랜치 없거나 뒤처져 있으면 `git push -u origin <branch>` 실행.
- **`--force` 푸시는 절대 금지** (git-instructions.md).

## PR 작성

### 5. 템플릿 로드
`.github/PULL_REQUEST_TEMPLATE.md`를 Read로 읽고 구조 파악.

### 6. 본문 채우기
모든 커밋(범위: 베이스 브랜치~HEAD)을 분석해 채운다:
- **작업 내용**: 무엇을 했는가 (커밋들의 종합, "최근 커밋"만 보지 말 것)
- **작업 이유**: 왜 (이슈/대화 맥락 기반)
- **관련 이슈**: `Closes #<번호>` (사용자 인자에서 추출, 없으면 비움)
- **변경 사항**: 핵심 변경 bullet
- **테스트**: 어떻게 검증했는지
- 체크리스트는 실제 통과한 항목만 체크

### 7. 제목 작성
Conventional Commits 형식 그대로:
- `feat(scope): subject`
- `fix(scope): subject`
- `refactor(scope): subject` 등
- 70자 이내, 한국어로

### 8. PR 생성
```bash
gh pr create \
  --base develop \
  --title "feat(settlement): 정산 배치 멱등성 보장" \
  --body "$(cat <<'EOF'
## 작업 내용
...
EOF
)"
```

기본 base는 `develop` (Git Flow). 사용자가 `--base main` 명시하면 따른다.

### 9. 결과 출력
생성된 PR URL을 사용자에게 보여준다.

## 주의사항
- **절대 main/develop으로 직접 푸시·머지하지 않는다.**
- `--no-verify`, `--no-gpg-sign` 등 훅/서명 우회 금지.
- 리베이스/히스토리 변경 금지 (이미 푸시된 커밋).
