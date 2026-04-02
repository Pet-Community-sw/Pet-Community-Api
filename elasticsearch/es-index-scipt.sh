#!/bin/bash

ES_URL="localhost:9200"
NEW_INDEX="members_v6"
NEW_INDEX_FILE="elasticsearch/members/v6/indexV6.json"
ALIAS_NAME="members"


echo  "1. 인덱스 생성"
curl -X PUT "$ES_URL/$NEW_INDEX" \
      -H "Content-Type: application/json" \
      -d @"$NEW_INDEX_FILE"

echo "2. Alias 설정"
curl -X POST "$ES_URL/_aliases" \
     -H "Content-Type: application/json" \
     -d @- <<EOF
     {
       "actions": [
         { "add": { "index": "$NEW_INDEX", "alias": "$ALIAS_NAME" } }
       ]
     }
EOF