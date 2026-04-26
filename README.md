# 🐶 멍냥로드

멍냥로드는 반려견 보호자들이 정보를 공유하고, 함께 산책할 사용자를 찾으며, 실시간으로 소통할 수 있도록 지원하는 반려동물 커뮤니티 서비스입니다.

프론트엔드 1명, 백엔드 1명이 함께 진행한 팀 프로젝트이며, 저는 백엔드 개발을 담당했습니다.

---

## 주요 기능

- 게시글, 댓글, 좋아요 기반 커뮤니티 기능
- 산책 친구 모집 및 신청 기능
- 1:1 및 그룹 실시간 채팅
- 사용자 위치 이벤트 처리 및 실시간 위치 공유
- 알림 기능
- 회원 검색 기능

---

## 기술 스택

- Language: Java
- Framework: Spring Boot, Spring Data JPA
- Database: MySQL, Redis, Elasticsearch
- Message Broker: RabbitMQ
- CDC: Debezium
- Realtime: WebSocket, STOMP

---

## 시작하기

### 사전 요구사항

- Java 17
- Docker

### 설치 방법

### 1. 저장소 클론

```bash
git clone https://github.com/Pet-Community-sw/Pet-Community-Api.git
cd Pet-Community-Api
```

### 2. 환경 변수 파일 생성

`.env.example` 파일을 복사한 뒤, 환경에 맞게 값을 입력해주세요.

```bash
cp .env.example .env
```

### 3. 실행 스크립트 권한 부여

```bash
chmod +x ./init-script.sh
```

### 4. 인프라 실행 및 Elasticsearch 인덱스 생성

아래 명령어를 실행하면 Docker 컨테이너가 실행되고, Elasticsearch가 준비될 때까지 대기한 뒤 인덱스를 생성합니다.

```bash
./init-script.sh
```

### 5. Spring Boot 애플리케이션 실행

```bash
./gradlew bootRun
```

## Swagger

- Swagger UI: `http://localhost:8080/swagger`

---

## System Architecture

<img width="858" height="534" alt="system architecture" src="https://github.com/user-attachments/assets/6554e787-95e6-4056-8631-e291fae932ed" />


---

## 기술적 고민과 해결

### 1. 부가 작업 처리로 인해 응답 시간이 불규칙하고 예측이 어려운 문제

- Architecture

<img width="676" height="452" alt="스크린샷 2026-02-26 20 10 10" src="https://github.com/user-attachments/assets/b3c75c95-d749-4e7d-8d87-0c2de00b9fb2" />

- Sequence Diagram

<img width="1114" height="560" alt="스크린샷 2026-02-25 22 19 54" src="https://github.com/user-attachments/assets/8e4ecd0f-1d31-4126-97a6-b74d98a53e2a" />
초기에는 알림, 메일, 검색 색인과 같은 부가 기능도 주요 비즈니스 로직과 같은 동기 흐름에서 처리했습니다.  
이 구조에서는 트래픽이 증가할수록 부가 로직의 지연이 전체 응답 시간에 영향을 주고, 스레드 풀 및 DB 커넥션 고갈로 이어질 위험이 있었습니다.

이를 개선하기 위해 비동기 기반 처리 구조를 도입했고, 최종적으로는 다음과 같은 흐름으로 발전시켰습니다.

- 초기: @Async 기반 비동기 처리
- 개선: RabbitMQ 도입으로 외부 메시지 브로커 기반 처리
- 추가 개선: Outbox Pattern 적용으로 DB 저장과 이벤트 발행 간 유실 가능성 완화
- 추가 개선: CDC 기반 구조로 전환하여 폴링 부하를 줄이고 실시간성을 높임
- 최종 개선: 이벤트 id 기반 멱등 처리 및 최신 이벤트 검증을 통해 중복 소비와 순서 역전 문제 방지

성능 측면에서도 최종 구조는 기존 동기 처리 방식 대비 p99 응답시간을 약 13% 개선했습니다. 동시에 이벤트 유실 가능성을 낮추고, 장애 발생 시에도 실패한 작업만 재처리할 수 있는 구조를 마련했습니다

- 개선 전
  <img width="1281" height="394" alt="동기" src="https://github.com/user-attachments/assets/14086d75-8dd1-4a06-b0bd-c98628ea55bb" />
- 개선 후
  <img width="1270" height="382" alt="최신 구조" src="https://github.com/user-attachments/assets/9356383f-ca08-466c-b8ef-f91896cdfeea" />

---

### 2. 초 단위 위치 이벤트로 인한 서버 부하 및 중복 처리 문제

- Sequence Diagram

<img width="844" height="696" alt="rxjava" src="https://github.com/user-attachments/assets/4b46f1d0-982d-42c2-9ff5-44bb284661ea" />


위치 이벤트는 사용자의 한 번의 요청으로 끝나는 단건이 아니라 산책 중 짧은 주기로 계속 유입되는 연속 이벤트였습니다.

따라서 모든 위치 이벤트를 동일하게 처리하면 위치 저장, 거리 계산, 실시간 전송, 범위 이탈 여부 판단이 매번 반복되어 서버 부하가 커질 수 있었습니다. 또한 GPS 오차나 제자리 근처의 미세한 좌표 변화처럼 실제
의미가 크지 않은 이벤트까지 처리 대상에 포함될 수 있었습니다.

이 문제를 줄이기 위해 위치 이벤트를 단순 요청이 아닌 스트림 데이터로 보고 RxJava 기반 비동기 스트림 파이프라인을 설계했습니다.

- 사용자별 파이프라인을 1회만 생성하도록 동시성 제어
- 2초 단위 throttling 적용
- 이동 거리 임계값 기반 필터링으로 의미 없는 GPS 오차 제거
- I/O 작업 분리
- 상태 변화 시점에만 알림이 발생하도록 distinctUntilChanged 적용
- timeout 기반 자동 정리로 메모리 누수 방지

이를 통해 고빈도 위치 이벤트 환경에서도 불필요한 처리와 중복 연산을 줄일 수 있도록 개선했습니다.

---

### 3. RDBMS 기반 부분 검색의 풀 스캔으로 인한 검색 성능 저하 문제

회원 검색 기능에서는 사용자가 이름 전체를 정확히 입력하지 않아도 원하는 사용자를 찾을 수 있도록 prefix 검색, 부분 포함 검색, 초성 검색을 지원하고자 했습니다.

초기에는 MySQL LIKE 기반으로 검색을 구현했지만 부분 포함 검색(LIKE '%keyword%')에서는 인덱스의 시작 지점을 특정할 수 없어 인덱스 전체 스캔이 발생했습니다. 이로 인해 데이터가
증가할수록 검색 비용이 함께 증가하는 한계가 있었습니다.

이를 해결하기 위해 검색 전용 엔진인 Elasticsearch를 도입하고 edge_ngram, ngram 분석기를 적용해 prefix 검색과 부분 검색을 처리하도록 개선했습니다.

다만 ngram 분석기는 문자열을 여러 토큰으로 분해해 저장하기 때문에 검색 기능은 개선되었지만 인덱스 저장 용량이 증가하는 문제가 있었습니다. 이를 줄이기 위해 다음과 같이 매핑을 최적화했습니다.

- 스코어 계산이 필요하지 않은 필드에 norms: false 적용
- 위치 정보가 필요하지 않은 필드에 index_options: docs 적용
- 검색, 정렬, 집계에 사용하지 않는 필드에 index: false, doc_values: false 적용

그 결과 동일 데이터 1만 건 기준으로 프라이머리 인덱스 저장 용량을 약 20% 절감했습니다.
<img width="884" height="60" alt="스크린샷 2026-02-20 23 00 49" src="https://github.com/user-attachments/assets/2e5b0240-91f2-49bd-9f9f-51e8dc454d17" />

또한 자동완성 검색은 사용자가 입력할 때마다 짧은 주기로 반복 호출되는 특성이 있어 동일하거나 유사한 요청이 자주 발생했습니다.
Elasticsearch로 전달되는 반복 요청을 줄이고 응답 시간을 개선하기 위해 Redis 캐시를 추가로 적용했습니다.

- MySQL LIKE 기반 검색
  <img width="1230" height="389" alt="mysql 그라파나 성능" src="https://github.com/user-attachments/assets/97ddc311-9361-465d-9ec7-6a237cddaf58" />
  부분 포함 검색에서 인덱스 풀 스캔으로 인해 p99 응답시간이 약 0.149초까지 상승했습니다.


- Elasticsearch 기반 검색
  <img width="1264" height="391" alt="es 그라파나 성능" src="https://github.com/user-attachments/assets/1f9bf1a6-3c5d-44fe-ab3d-da5fe45d94be" />
  Elasticsearch 도입으로 p99 응답시간을 약 0.088초까지 낮췄습니다. 이는 MySQL 대비 약 41% 개선했습니다.


- Elasticsearch + Redis 캐시 적용
  <img width="1254" height="413" alt="es redis 성능" src="https://github.com/user-attachments/assets/0d6ba99c-38fe-4bc0-9696-24b86b8828b3" />
  Redis 캐시를 적용해 p99 응답시간을 약 0.05초까지 낮췄습니다. 이는 Elasticsearch 대비 약 43%, MySQL 대비 약 66% 개선된 수치입니다.

---

### 4. STOMP 메시지 처리 시 분기 로직 복잡도 증가 문제

실시간 채팅 기능에서는 STOMP 요청이 CONNECT, SUBSCRIBE, SEND, DISCONNECT 등 command 종류에 따라 다르게 동작하며,
특히 SUBSCRIBE의 경우에도 destination이 고정값이 아니라 /sub/chat/{chatRoomId}와 같이 path variable을 포함한 가변 경로 형태였기 때문에,
단순 문자열 비교만으로는 분기 처리에 한계가 있었습니다.

초기에는 switch 기반 분기 구조를 고려했지만, command와 경로가 늘어날수록 유지보수성이 급격히 떨어질 수 있다고 판단했습니다.

이를 해결하기 위해 다음과 같은 방식으로 구조를 개선했습니다.

- command 단위는 전략 패턴으로 분리
- SUBSCRIBE 내부 destination 가변 경로는 AntPathMatcher를 활용한 전략 패턴으로 분리

이 구조를 통해 새로운 command나 구독 경로가 추가되더라도 기존 코드를 수정하지 않고 확장 가능하도록 개선했습니다.

---

### 5. 게시글 목록 조회 시 JPA N+1 문제 해결

게시글 목록 조회 과정에서 작성자 정보, 좋아요 여부 등 연관 데이터를 함께 조회해야 했고,
지연 로딩된 엔티티에 반복 접근하면서 N+1 문제가 발생했습니다.

목록 API는 페이지 단위 조회가 필요했는데, 1:N 관계에서 fetch join과 페이징을 함께 사용할 경우 DB 레벨이 아닌 애플리케이션 메모리에서 페이징이 수행될 수 있었습니다.

따라서 최종적으로는 화면에 필요한 값만 직접 조회하는 Projection 기반 조회 방식으로 전환했습니다.

이를 통해 N+1 문제를 해결하고 실제 화면에 필요한 값만 조회하며 목록 조회에 적합한 구조로 개선했습니다.

---

## 포트폴리오

프로젝트의 상세한 기술 선정 이유, 설계 배경, 문제 해결 과정, 성능 개선 내용은 아래 문서에 정리했습니다.

### [📘포트폴리오]( https://www.notion.so/Project-Portfolio-329efd57b2f78066a524e7942a8756e2?source=copy_link )



