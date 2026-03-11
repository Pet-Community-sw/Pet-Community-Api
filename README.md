# 🐶 멍냥로드

반려견 보호자들을 위한 커뮤니티 및 산책 매칭 서비스의 백엔드 서버입니다.  
사용자 인증, 게시글, 실시간 채팅, 알림, 위치 이벤트 처리, 검색 기능을 제공합니다.

---

## 프로젝트 소개

이 프로젝트는 반려견 보호자들이 정보를 공유하고, 함께 산책할 사용자를 찾고, 실시간으로 소통할 수 있도록 지원하는 서비스입니다.  
백엔드에서는 단순 CRUD를 넘어, 실시간 채팅, 비동기 이벤트 처리, 위치 이벤트 스트림 제어, 검색 최적화와 같은 문제를 해결하는 데 중점을 두었습니다.

---

## 주요 기능

- 게시글, 댓글, 좋아요 기반 커뮤니티 기능
- 1:1 및 그룹 실시간 채팅
- 사용자 위치 이벤트 처리
- 회원 검색 및 자동완성
- 실시간 알림

---

## 기술 스택

- Language: Java
- Framework: Spring Boot, Spring Data JPA
- Database / Cache: MySQL, Redis
- Search Engine: Elasticsearch
- Message Broker / CDC: RabbitMQ, Debezium
- Realtime: WebSocket

---

## 기술적 고민과 해결

### 1. 대용량 트래픽 환경에서 응답 지연을 줄이기 위한 비동기 처리 구조 도입

<img width="676" height="452" alt="스크린샷 2026-02-26 20 10 10" src="https://github.com/user-attachments/assets/b3c75c95-d749-4e7d-8d87-0c2de00b9fb2" />

초기에는 알림, 메일, 검색 색인과 같은 부가 기능도 주요 비즈니스 로직과 같은 동기 흐름에서 처리했습니다.  
이 구조에서는 트래픽이 증가할수록 부가 로직의 지연이 전체 응답 시간에 영향을 주고, 스레드 풀 및 DB 커넥션 고갈로 이어질 위험이 있었습니다.

이를 개선하기 위해 비동기 기반 처리 구조를 도입했고, 최종적으로는 다음과 같은 흐름으로 발전시켰습니다.

- 초기: @Async 기반 비동기 처리
- 개선: RabbitMQ 도입으로 외부 메시지 브로커 기반 처리
- 추가 개선: Outbox Pattern 적용으로 DB 저장과 이벤트 발행 간 유실 가능성 완화
- 최종 개선: CDC 기반 구조로 전환하여 폴링 부하를 줄이고 실시간성을 높임

이를 통해 요청-응답 흐름과 부가 작업을 분리하고 장애 상황에서도 이벤트 유실 가능성을 줄이며 실패 케이스만 별도로 처리하는 구조를 설계했습니다.

- Sequence Diagram

<img width="1114" height="560" alt="스크린샷 2026-02-25 22 19 54" src="https://github.com/user-attachments/assets/8e4ecd0f-1d31-4126-97a6-b74d98a53e2a" />

---

### 2. 초 단위 위치 이벤트로 인한 서버 부하 및 중복 처리 문제

- Sequence Diagram

<img width="913" height="815" alt="스크린샷 2026-03-01 21 56 30" src="https://github.com/user-attachments/assets/4b08d92e-55b3-461c-bd46-aadd4588477d" />

위치 이벤트는 단건 요청이 아니라 연속적으로 유입되는 스트림 데이터이기 때문에,
일반적인 요청 처리 방식보다 이벤트 폭주 제어, 순서 보장, 중복 제거가 중요했습니다.

이를 위해 RxJava 기반 비동기 스트림 파이프라인을 설계했습니다.

- 사용자별 파이프라인을 1회만 생성하도록 동시성 제어
- 2초 단위 throttling 적용
- Backpressure(LATEST)로 최신 이벤트만 반영
- 이동 거리 임계값 기반 필터링으로 의미 없는 GPS 오차 제거
- 상태 변화 시점에만 알림이 발생하도록 distinctUntilChanged 적용
- timeout 기반 자동 정리로 메모리 누수 방지

이를 통해 고빈도 위치 이벤트 환경에서도 불필요한 처리와 중복 연산을 줄일 수 있도록 개선했습니다.

---

### 3. Elasticsearch 인덱스 메모리 최적화 및 자동완성 성능 개선

초기에는 MySQL LIKE 기반 검색을 사용했지만, 부분 포함 검색 시 인덱스 전체 스캔이 발생해 데이터 증가에 따라 검색 비용이 커지는 문제가 있었습니다.  
이를 해결하기 위해 Elasticsearch를 도입했습니다.

다만 자동완성, 부분 포함 검색, 초성 검색 등을 위해 edge_ngram, ngram 기반 분석기를 적용하자 토큰 수 증가로 인해 인덱스 저장 비용이 커지는 문제가 발생했습니다.

이를 개선하기 위해 다음과 같은 최적화를 적용했습니다.

- norms: false
- index_options: docs
- 사용하지 않는 필드에 대해 index: false, doc_values: false 적용

그 결과 동일 데이터 1만 건 기준으로 프라이머리 인덱스 저장 용량을 약 20% 절감했습니다.

또한 반복 호출이 많은 자동완성 특성을 고려해 Redis 캐시를 적용하여 p99 응답시간을 약 60% 개선했습니다.

- Redis 도입 전

<img width="1277" height="412" alt="스크린샷 2026-03-07 14 19 49" src="https://github.com/user-attachments/assets/14f7c918-dfa6-4cc0-bfd5-33390f8adf5f" />

- Redis 도입 후

<img width="1279" height="406" alt="스크린샷 2026-03-07 14 21 22" src="https://github.com/user-attachments/assets/63138de8-e715-4697-ae61-66bd86f7e727" />

---

### 4. STOMP 메시지 처리 시 분기 로직 복잡도 증가 문제

실시간 채팅 기능에서는 STOMP 요청이 CONNECT, SUBSCRIBE, SEND, DISCONNECT 등 command 종류에 따라 다르게 동작하며,  
특히 SUBSCRIBE는 destination 경로에 따라 또 다른 세부 로직이 필요했습니다.

초기에는 switch 기반 분기 구조를 고려했지만, command와 경로가 늘어날수록 유지보수성이 급격히 떨어질 수 있다고 판단했습니다.

이를 해결하기 위해

- command 단위는 전략 패턴으로 분리
- SUBSCRIBE 내부 destination 분기는 SubscribeTypeStrategy로 분리
- 가변 경로 매칭은 AntPathMatcher를 활용

하도록 설계했습니다.

이 구조를 통해 새로운 command나 구독 경로가 추가되더라도 기존 코드를 크게 수정하지 않고 확장 가능하도록 개선했습니다.

---

### 5. 게시글 목록 조회 시 JPA N+1 문제 해결

게시글 목록 조회 과정에서 작성자 정보, 좋아요 여부 등 연관 데이터를 함께 조회해야 했고,
지연 로딩된 엔티티에 반복 접근하면서 N+1 문제가 발생했습니다.

처음에는 fetch join을 고려했지만,
목록 API는 페이지 단위 조회가 필요했고 1:N 관계에서 fetch join과 pagination을 함께 사용할 경우 메모리 페이징 문제가 발생할 수 있었습니다.

따라서 최종적으로는 화면에 필요한 값만 직접 조회하는 Projection 기반 조회 방식으로 전환했습니다.

이를 통해 N+1 문제를 줄이고 실제 화면에 필요한 값만 조회하며 목록 조회에 적합한 구조로 개선했습니다.

