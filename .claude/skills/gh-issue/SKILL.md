---
name: gh-issue
description: GitHub 이슈를 템플릿(.github/ISSUE_TEMPLATE/) 기반으로 생성합니다. 사용자가 이슈/issue/버그 제보/기능 요청을 만들고 싶다고 할 때 호출됩니다.
allowed-tools: Bash(gh issue *), Bash(gh repo *), Read
---

# GitHub Issue 생성

`$ARGUMENTS`로 전달받은 내용으로 GitHub 이슈를 생성합니다.

## 절차

### 1. 이슈 타입 결정
다음 중 하나로 분류 (사용자 입력에서 추론, 애매하면 AskUserQuestion으로 확인):
- **bug** → `.github/ISSUE_TEMPLATE/bug_report.md`
- **feature** → `.github/ISSUE_TEMPLATE/feature_request.md`
- **refactor** → `.github/ISSUE_TEMPLATE/refactor.md`

### 2. 템플릿 로드
선택된 템플릿 파일을 Read로 읽어 frontmatter 아래의 본문 구조를 파악한다.

### 3. 본문 채우기
사용자가 준 내용 + 대화 맥락을 토대로 템플릿의 각 섹션을 채운다.
- 채울 정보가 부족한 핵심 섹션이 있으면 사용자에게 한 번에 모아서 묻는다.
- 채우지 못한 선택 섹션은 `<!-- 해당 없음 -->`으로 표시하거나 비워둔다.
- frontmatter(`---` 블록)는 제거하고 본문만 사용한다.

### 4. 제목 작성
프로젝트 컨벤션에 맞춘 prefix:
- Bug: `[Bug] <한 줄 요약>`
- Feature: `[Feature] <한 줄 요약>`
- Refactor: `[Refactor] <한 줄 요약>`

### 5. 이슈 생성
`gh issue create`로 생성한다. body는 HEREDOC로 전달:

```bash
gh issue create \
  --title "[Feature] 정산 배치 멱등성 보장" \
  --label "enhancement" \
  --body "$(cat <<'EOF'
## 배경 / 문제 상황
...
EOF
)"
```

라벨은 템플릿 frontmatter의 `labels:` 값을 그대로 사용한다.

### 6. 생성 결과 출력
생성된 이슈 URL과 번호를 사용자에게 보여준다.

## 주의사항
- **절대 임의로 푸시/브랜치 생성하지 않는다.** 이 skill은 이슈 생성만 담당한다.
- `gh` CLI가 설치/인증되어 있지 않으면 실행 전에 사용자에게 알린다.
- 이슈 본문은 한국어로 작성하되, 코드/식별자/에러 메시지는 원문 유지.
