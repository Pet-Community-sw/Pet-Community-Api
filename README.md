# 🐶 멍냥로드 API Server

멍냥로드는 반려견 보호자들이 정보를 공유하고, 함께 산책할 사용자를 찾으며, 실시간으로 소통할 수 있도록 지원하는 반려동물 커뮤니티 서비스입니다.

프론트엔드 1명, 백엔드 1명이 함께 진행한 팀 프로젝트이며, 백엔드 개발을 담당했습니다.

---

## 기술 스택

- Language: Java
- Framework: Spring Boot, Spring Data JPA
- Database: MySQL, Redis
- Realtime: WebSocket, STOMP
- Cloud: AWS EC2, RDS, S3, CloudFront
- CI/CD: GitHub Actions

---

## 시작하기

### 사전 요구사항

- Java 21
- Docker

### 설치 방법

### 1. 저장소 클론

```bash
git clone https://github.com/Pet-Community-sw/Pet-Community-Api.git
cd Pet-Community-Api
```

### 2. 스크립트 실행

```bash
bash ./init-script.sh
```

## Swagger

- Swagger UI: `http://localhost:8080/swagger`

---

## System Architecture

<img width="603" height="524" alt="System Architecutre" src="https://github.com/user-attachments/assets/f722e08b-1d73-4c10-90fa-1da3a7fe4763" />


---

## CI/CD Architecture

<img width="1004" height="506" alt="CI:CD Architecture" src="https://github.com/user-attachments/assets/e1b8f076-55c6-4603-95df-1533f57e84db" />

---

## 기술적 고민과 해결

### 1. 핵심 요청 흐름에 포함된 부가 작업으로 인한 응답 성능 저하 개선

- Architecture

<img width="676" height="452" alt="스크린샷 2026-02-26 20 10 10" src="https://github.com/user-attachments/assets/b3c75c95-d749-4e7d-8d87-0c2de00b9fb2" />

- Sequence Diagram

<img width="1048" height="540" alt="Screenshot 2026-05-16 at 19 07 28" src="https://github.com/user-attachments/assets/855cee65-b28f-46cf-93d4-17c5af1013f9" />
초기에는 알림, 메일, 검색 색인과 같은 부가 기능도 주요 비즈니스 로직과 같은 동기 흐름에서 처리했습니다.  
이 구조에서는 트래픽이 증가할수록 부가 로직의 지연이 전체 응답 시간에 영향을 주고, 스레드 풀 및 DB 커넥션 고갈로 이어질 위험이 있었습니다.

이를 개선하기 위해 비동기 기반 처리 구조를 도입했고, 최종적으로는 다음과 같은 흐름으로 발전시켰습니다.

- 초기: @Async 기반 비동기 처리
- 개선: RabbitMQ 도입으로 외부 메시지 브로커 기반 처리
- 추가 개선: Outbox Pattern 적용으로 DB 저장과 이벤트 발행 간 유실 가능성 완화
- 추가 개선: CDC 기반 구조로 전환하여 폴링 부하를 줄이고 실시간성을 높임
- 추가 개선: 이벤트 id 기반 멱등 처리 및 최신 이벤트 검증을 통해 중복 소비와 순서 역전 문제 방지
- 최종 개선: Worker Server를 분리하여 API 요청 처리와 부가 작업 처리 책임 분리

[Worker Server Github Link](https://github.com/Pet-Community-sw/Pet-Community-Worker)

기존 동기 구조와 최종 개선 구조의 응답 성능을 비교하기 위해 k6를 사용해 부하 테스트를 진행했습니다.

- 개선 전

<img width="1042" height="317" alt="동기 k6" src="https://github.com/user-attachments/assets/fd2e8398-0150-4d08-af26-ab2a81df0b9f" />
동기 구조에서는 p99 472.56ms가 측정되었습니다.

- 개선 후

<img width="1034" height="381" alt="아웃박스 k6" src="https://github.com/user-attachments/assets/c92574d8-c54a-43a1-8745-228712aa689a" />
Worker Server 분리 후 p99 343.59ms로 감소했습니다. 이는 기존 동기 구조에서 약 27% 개선된 수치입니다. 

응답 성능 개선뿐만 아니라, API 서버는 핵심 요청 처리를 계속 수행할 수 있어 장애 격리 측면에서도 더 안정적인 구조라고 생각합니다.

---

### 2. RDBMS 기반 검색 성능 한계 개선

회원 검색 기능에서는 사용자가 이름 전체를 정확히 입력하지 않아도 원하는 사용자를 찾을 수 있도록 prefix 검색, 부분 포함 검색, 초성 검색을 지원하고자 했습니다.

초기에는 MySQL LIKE 기반으로 검색을 구현했지만 부분 포함 검색(LIKE '%keyword%')에서는 인덱스의 시작 지점을 특정할 수 없어 인덱스 전체 스캔이 발생했습니다. 이로 인해 데이터가
증가할수록 검색 비용이 함께 증가하는 한계가 있었습니다.

이를 해결하기 위해 검색 전용 엔진인 Elasticsearch를 도입하고 edge_ngram, ngram 분석기를 적용해 prefix 검색과 부분 검색을 처리하도록 개선했습니다.

다만 ngram 분석기는 문자열을 여러 토큰으로 분해해 저장하기 때문에 검색 기능은 개선되었지만 인덱스 저장 용량이 증가하는 문제가 있었습니다. 이를 줄이기 위해 다음과 같이 매핑을 최적화했습니다.

- 스코어 계산이 필요하지 않은 필드에 norms: false 적용
- 위치 정보가 필요하지 않은 필드에 index_options: docs 적용
- 검색, 정렬, 집계에 사용하지 않는 필드에 index: false, doc_values: false 적용

그 결과 동일 데이터 1만 건 기준으로 프라이머리 인덱스 저장 용량을 약 20% 절감했습니다.
<img width="884" height="60" alt="스크린샷 2026-02-20 23 00 49" src="https://github.com/user-attachments/assets/2e5b0240-91f2-49bd-9f9f-51e8dc454d17" />

자동완성 검색은 사용자가 한 글자씩 입력할 때마다 prefix 검색 요청이 짧은 주기로 반복해서 발생하는 특성이 있습니다.
이러한 요청을 매번 Elasticsearch까지 전달하기보다 중간에 캐싱 계층을 두면, 반복 조회를 중간에서 먼저 처리할 수 있어 응답 성능 개선을 기대할 수 있다고 판단했습니다.
Elasticsearch로 전달되는 반복 요청을 줄이고 응답 시간을 개선하기 위해 Redis 캐시를 추가로 적용했습니다.

검색 방식별 응답 성능을 비교하기 위해 MySQL 기반 검색, Elasticsearch 기반 검색, Elasticsearch + Redis 캐시 적용 구조를 대상으로 k6 부하 테스트를 진행했습니다.

- MySQL

<img width="960" height="387" alt="mysql 부분검색 k6" src="https://github.com/user-attachments/assets/ac2319d1-b32e-4f44-883e-a655cef671e5" />
인덱스 풀 스캔으로 인해 p99 20.28ms로 측정되었습니다.

- Elasticsearch 기반 검색

<img width="1006" height="388" alt="es 부분검색 k6" src="https://github.com/user-attachments/assets/327c7e03-eb04-47b9-9d92-9268f63b960b" />
Elasticsearch 도입 후 p99 4.71ms로 감소했습니다. 이는 MySQL 대비 약 76% 개선된 수치입니다.

- Elasticsearch + Redis 캐시 적용

<img width="1030" height="378" alt="es + redis prefix k6" src="https://github.com/user-attachments/assets/b29a6a37-74ad-423b-9472-230269c45260" />
Redis 캐시 적용 후 p99 2.1ms로 감소했습니다. 이는 Elasticsearch 대비 약 54%개선된 수치입니다.

---

### 3. JWT 로그아웃 이후 Access Token 재사용 문제 개선

회원 전용 API에서 요청 사용자를 식별하고 권한을 확인하기 위해 JWT 기반 인증 방식을 적용했습니다.  
JWT는 서버가 사용자별 인증 상태를 저장하지 않아도 토큰 검증만으로 인증을 처리할 수 있다는 장점이 있지만, 로그아웃 이후 발급된 Access Token을 즉시 무효화하기 어렵다는 한계가 있었습니다.
이를 보완하기 위해 로그아웃된 Access Token을 Redis에 저장했습니다.

- Architecture

<img width="743" height="572" alt="Screenshot 2026-05-14 at 00 14 29" src="https://github.com/user-attachments/assets/80dc704e-a7ea-4a28-9ac3-927a146cc35c" />

로그아웃 요청 시 Access Token을 검증한 뒤 해당 사용자의 Refresh Token을 제거하고, 로그아웃된 Access Token을 Redis에 저장했습니다. 이때 TTL은 토큰의 기본 만료 시간과 동일하게
설정했습니다. 이후 요청 시 Redis에서 해당 Access Token의 존재 여부를 확인하고, 로그아웃 처리된 토큰이면 요청을 거부하도록 구성했습니다.

---

### 4. 메시지 처리 구조의 분기 복잡도 개선

실시간 채팅 기능에서는 STOMP 요청이 CONNECT, SUBSCRIBE, SEND, DISCONNECT 등 command 종류에 따라 다르게 동작하며,
특히 SUBSCRIBE의 경우에도 destination이 고정값이 아니라 /sub/chat/{chatRoomId}와 같이 path variable을 포함한 가변 경로 형태였기 때문에,
단순 문자열 비교만으로는 분기 처리에 한계가 있었습니다.

초기에는 switch 기반 분기 구조를 고려했지만, command와 경로가 늘어날수록 유지보수성이 급격히 떨어질 수 있다고 판단했습니다.

이를 해결하기 위해 다음과 같은 방식으로 구조를 개선했습니다.

- command 단위는 전략 패턴으로 분리
- SUBSCRIBE 내부 destination 가변 경로는 AntPathMatcher를 활용한 전략 패턴으로 분리

이 구조를 통해 새로운 command나 구독 경로가 추가되더라도 기존 코드를 수정하지 않고 확장 가능하도록 개선했습니다.

---

### 5. 게시글 목록 조회 시 연관 데이터 조회 과정에서 발생한 JPA N+1 문제

게시글 목록 조회 과정에서 작성자 정보, 좋아요 여부 등 연관 데이터를 함께 조회해야 했고,
지연 로딩된 엔티티에 반복 접근하면서 N+1 문제가 발생했습니다.

목록 API는 페이지 단위 조회가 필요했는데, 1:N 관계에서 fetch join과 페이징을 함께 사용할 경우 DB 레벨이 아닌 애플리케이션 메모리에서 페이징이 수행될 수 있었습니다.

따라서 최종적으로는 화면에 필요한 값만 직접 조회하는 Projection 기반 조회 방식으로 전환했습니다.

이를 통해 N+1 문제를 해결하고 실제 화면에 필요한 값만 조회하며 목록 조회에 적합한 구조로 개선했습니다.

---

### 6. S3 직접 접근 구조를 CloudFront 기반 캐싱 구조로 개선

기존에는 프로필 이미지, 게시글 이미지와 같은 정적 리소스를 Amazon S3에 저장하고, API 서버가 S3 URL을 그대로 반환하는 구조였습니다.

이미지 리소스는 한 번 업로드되면 자주 변경되지 않고 여러 사용자가 반복적으로 조회하는 특성이 있어, CloudFront를 도입해 캐싱 계층에서 정적 리소스 요청을 처리하도록 개선했습니다.

- Architecture

<img width="813" height="382" alt="s3 + cloud" src="https://github.com/user-attachments/assets/4f530884-e418-4aa2-ae22-10221ebe400d" />

이미지 업로드 시에는 API 서버가 파일을 S3에 저장하고 CloudFront 도메인 URL을 반환합니다. 이후 이미지 조회 요청은 CloudFront를 통해 처리되며, 캐시된 리소스가 있으면 CloudFront에서
바로 응답하고 캐시가 없는 경우에만 S3에서 원본을 조회합니다.

### 6-1) 성능 측정

k6를 사용해 동일한 이미지 리소스에 대해 가상 사용자 5명, 총 100회의 GET 요청을 수행했습니다. 애플리케이션 서버의 처리 시간을 제외하고 정적 리소스 조회 성능만 비교하기 위해 S3 URL과
CloudFront URL에 직접 요청을 보내는 방식으로 측정했습니다.

- S3

<img width="910" height="418" alt="s3 성능" src="https://github.com/user-attachments/assets/b22d03ca-21d7-4445-b299-e0f7be9b04d1" />

- S3 + CloudFront

<img width="885" height="410" alt="s3 + cloudfront성능" src="https://github.com/user-attachments/assets/81ecafa6-2a4d-4675-8ae3-651e898f6661" />

CloudFront 적용 후 p99 응답 시간이 63.73ms에서 26.93ms로 감소했으며, 약 57.7% 개선된 것을 확인했습니다.

### 6-2) 비용 개선

S3 직접 접근 방식은 이미지 조회 요청이 발생할 때마다 S3 GET 요청 비용이 누적되는 구조였습니다. 반면 CloudFront Free 플랜은 월 100만 요청과 100GB 데이터 전송을 무료 사용량으로
제공하며, CloudFront와 S3 간 데이터 전송 비용도 면제됩니다.

이를 통해 S3 직접 접근 방식 대비 비용 부담을 완화할 수 있는 구조로 개선했습니다.

---

## 포트폴리오

상세한 기술 선정 이유, 설계 배경, 문제 해결 과정, 성능 개선, 테스트 환경 내용은 아래 문서에 정리했습니다.

### [📘포트폴리오]( https://www.notion.so/Project-Portfolio-35aefd57b2f780989abec46b3ec46956?source=copy_link )



