#!/usr/bin/env bash
set -e

ELASTICSEARCH_URI="http://localhost:9200"
INDEX_NAME="members"
MAPPING_FILE="elasticsearch/members/v6/indexV6.json"

echo "1. docker compose 실행"
docker compose up -d

echo "2. Elasticsearch 준비 대기"
until curl -fsS "$ELASTICSEARCH_URI" >/dev/null; do
  sleep 2
done

echo "3. 인덱스 생성"
curl -X PUT "$ELASTICSEARCH_URI/$INDEX_NAME" \
  -H "Content-Type: application/json" \
  -d @"$MAPPING_FILE"

echo "4. boot 실행"
./gradlew bootRun
