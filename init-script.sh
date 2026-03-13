#!/usr/bin/env bash
set -e
set -a
source .env
set +a

INDEX_NAME="members"
MAPPING_FILE="elasticsearch/members/v5/indexV5.json"

echo "1. MySQL 먼저 실행"
docker compose up -d mysql

echo "2. MySQL 준비 대기"
until docker exec pet-app-mysql mysqladmin ping -h 127.0.0.1 -uroot -p"$MYSQL_ROOT_PASSWORD" --silent >/dev/null 2>&1; do
  echo "MySQL이 아직 준비되지 않았습니다. 대기 중..."
  sleep 2
done

echo "3. Debezium 계정 생성 및 권한 부여"
docker exec -i pet-app-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" <<SQL
CREATE USER IF NOT EXISTS '${DEBEZIUM_USER}'@'%' IDENTIFIED BY '${DEBEZIUM_PASSWORD}';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT
ON *.* TO '${DEBEZIUM_USER}'@'%';
FLUSH PRIVILEGES;
SQL

echo "4. 나머지 컨테이너 실행"
docker compose up -d

echo "5. Elasticsearch 준비 대기"
until curl -s "$ELASTICSEARCH_URI" >/dev/null; do
  echo "Elasticsearch/Nginx가 아직 준비되지 않았습니다. 대기 중..."
  sleep 2
done

echo "6. 인덱스 생성"
curl -X PUT "$ELASTICSEARCH_URI/$INDEX_NAME" \
  -H "Content-Type: application/json" \
  -d @"$MAPPING_FILE"

echo "7. 완료"