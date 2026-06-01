#!/usr/bin/env bash
set -e

set -a
source .env
set +a

ELASTICSEARCH_URI="http://localhost:9200"
INDEX_NAME="members"
MAPPING_FILE="elasticsearch/members/v6/indexV6.json"

echo "1. docker compose 실행"
docker compose up -d

echo "2. 인덱스 생성"
curl -X PUT "$ELASTICSEARCH_URI/$INDEX_NAME" \
  -H "Content-Type: application/json" \
  -d @"$MAPPING_FILE"

echo "3. boot 실행"
./gradlew bootRun