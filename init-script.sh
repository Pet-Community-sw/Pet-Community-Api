#!/usr/bin/env bash
set -e

ES_URL="http://localhost:80"
INDEX_NAME="members"
MAPPING_FILE="elasticsearch/members/v5/indexV5.json"


echo "1. Docker 컨테이너 실행"
docker compose up -d

echo "2. Elasticsearch 준비 대기"
until curl -s http://localhost:80 >/dev/null; do
  echo "Elasticsearch/Nginx가 아직 준비되지 않았습니다. 대기 중..."
  sleep 2
done

echo "3. 인덱스 생성"
curl -X PUT "$ES_URL/$INDEX_NAME" \
  -H "Content-Type: application/json" \
  -d @"$MAPPING_FILE"

echo "4. 완료"