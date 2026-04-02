#!/bin/bash

ES_URL="localhost:9200"
ALIAS_NAME="members"
NEW_INDEX="member_v6"
OLD_INDEX="member_v5"
NEW_INDEX_FILE="elasticsearch/members/v6/indexV6.json"

echo "1. 새로운 인덱스 생성 ($NEW_INDEX)"
curl -X PUT "$ES_URL/$NEW_INDEX" \
     -H "Content-Type: application/json" \
     -d @"$NEW_INDEX_FILE"

echo -e "\n2. 리인덱싱 시작 ($OLD_INDEX -> $NEW_INDEX) "
curl -X POST "$ES_URL/_reindex?wait_for_completion=true" \
     -H "Content-Type: application/json" \
     -d @- <<EOF
     {
       "source": { "index": "$OLD_INDEX" },
       "dest": { "index": "$NEW_INDEX" }
     }
EOF

echo -e "\n3. Alias 업데이트"
curl -X POST "$ES_URL/_aliases" \
     -H "Content-Type: application/json" \
     -d @- <<EOF
     {
       "actions": [
         { "remove": { "index": "$OLD_INDEX", "alias": "$ALIAS_NAME" } },
         { "add": { "index": "$NEW_INDEX", "alias": "$ALIAS_NAME" } }
       ]
     }
EOF

echo -e "\n4. 이전 인덱스 삭제 $OLD_INDEX"
curl -X DELETE "$ES_URL/$OLD_INDEX"

echo -e "\n5. completed"