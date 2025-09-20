# People of Delivery — Phase 1 (Monolith)

## 프로젝트 소개
배달의민족을 벤치마킹한 **주문·상점 관리 플랫폼**의 1차 단계입니다.  
Spring Boot 기반 **단일 애플리케이션(모놀리식)** 으로 고객의 **주문 → 결제 → 상태 추적**과 **상점/메뉴/회원 관리**의 핵심 기능을 구현했으며, 이후 MSA 전환(2차, 3차)을 고려해 도메인 경계를 명확히 했습니다.

---

## 개발 기간
- 25.07.21 ~ 25.08.06

---

## 개발 구성원 및 역할
- [조성규](https://github.com/sungchilll): 장바구니 도메인
- [김준형](https://github.com/jh010303): DB 설계, 초기 프로젝트 세팅, 주문 도메인, 리뷰 도메인
- [김지윤](https://github.com/JIYOOnii007): 가게 도메인
- [모시은](https://github.com/shiien14): DB 설계, 인증/인가, 회원 도메인, 결제 도메인
- [정병민](https://github.com/ByeongminJeong): 메뉴 도메인

---

## 기술 스택
- **Backend**: Java 21, Spring Boot, Spring MVC, Spring Data JPA
- **Auth**: **JWT**(액세스/리프레시), **Spring Security OAuth2 Client** *(Google)*  
- **Database**: **PostgreSQL**, **Redis**  
- **문서화**: Swagger/OpenAPI
- **테스트**: JUnit 5  
- **빌드/런타임**: Gradle, Docker/Docker Compose
- **CI**: GitHub Actions

---

## 핵심 목표
1. **핵심 사용자 흐름 완성**: 주문 생성 → 결제 처리 → 주문 상태 추적  
2. **도메인 중심 설계**: 주문(Order), 결제(Payment), 상점(Store), 메뉴(Menu), 회원(User) 경계 정의  
3. **역할 기반 요구사항 정리**: 고객/점주/운영자 관점의 기능 및 권한  
4. **품질 기반**: 단위/통합 테스트와 기본 API 문서로 안정적인 배포 준비  
5. **MSA 전환 대비**: 패키지 구조/설정 분리로 서비스 분리의 마찰 최소화

---

## 주요 기능
- **사용자 인증/인가**  
  - Spring Security + **JWT** 기반 로그인/권한 부여 (Access/Refresh 토큰 전략)  
  - **소셜 로그인(OAuth2)**: **Google 로그인 연동** → 최초 로그인 시 사용자 프로필 매핑/회원 생성 → 내부 **JWT 발급**  
    - 커스텀 `OAuth2UserService`로 **소셜 계정 ↔ 내부 계정** 연결  
- **이메일 인증**: 회원가입 시 인증 메일 발송 및 검증 흐름
- **매장 탐색**  
  - **지역 기반** 주변 매장 조회 
  - **카테고리별** 매장/메뉴 필터링
- **결제**: **토스(Toss) API 연동** 결제 승인/실패 처리 및 리다이렉트 플로우
- **주문 관리**: 장바구니 → 주문 생성 → 결제 연동 → 주문 상태 변경(예: CREATED → PAID → PREPARING …)
- **감사 필드 자동화**: JPA **Entity Listener**로 생성/수정 시간 자동 관리
- **표준 응답/예외 처리**: 공통 응답 스펙(성공/에러 코드)과 글로벌 예외 핸들러
- **API 문서**: Swagger UI로 주요 엔드포인트 확인

---

## 아키텍처 하이라이트
- **모놀리식 구조 + 도메인 경계 분리**: Controller → Service → Repository → Domain/DTO 계층화
- **단일 트랜잭션 일관성**: 주문·결제 핵심 플로우를 단일 DB 트랜잭션으로 보장
- **환경 분리 가능**: 로컬/개발/운영 환경을 프로퍼티로 분리하여 운영 편의성 확보
- **문서/테스트 기반**: Swagger(OpenAPI)와 JUnit 5로 기본 신뢰성 확보

---

## Commit Message Convention

| Tag Name       | Description                                    |
|----------------|------------------------------------------------|
| :sparkles: Feat    | 새로운 기능을 추가                              |
| :bug: Fix          | 버그 수정                                      |
| :art: Style        | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우 |
| :hammer:  Refactor | 프로덕션 코드 리팩토링                         |
| :memo: Docs        | 문서 수정                                      |
| :test_tube: Test   | 테스트 코드, 리팩토링 테스트 코드 추가, Production Code(실제로 사용하는 코드) 변경 없음 |
| :rocket: Chore     | 빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트, Production Code 변경 없음 |
