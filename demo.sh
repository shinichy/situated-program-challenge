#!/usr/bin/env bash
set -e

cd `dirname $0`

CLIENT_JAR=client/target/scala-2.12/client-assembly-1.0-SNAPSHOT.jar
BASE_URL=http://localhost:9000

# reset database
lein migratus reset

echo 'Create a member Shinichi'
echo '{
  "first-name": "Shinichi",
  "last-name": "Katayama",
  "email": "foo@foo.com"
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/members" POST | jq .

echo 'Create a member Kenji'
echo '{
  "first-name": "Kenji",
  "last-name": "Nakamura",
  "email": "k2n@foo.com"
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/members" POST | jq .

echo "Get members"
java -jar ${CLIENT_JAR} "${BASE_URL}/members" GET | jq .

echo 'Create a group clj-nakano'
echo '{
  "group-name": "clj-nakano",
  "admin-member-ids": [
    2
  ]
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/groups" POST | jq .

echo 'Shinichi joins clj-nakano with admin Kenji'
echo '{
  "admin": false
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/members/1/groups/1" POST | jq .

echo 'Create a venue ICTCO'
echo '{
  "venue-name": "ICTCO",
  "address": {
    "postal-code": "164-0001",
    "prefecture": "東京",
    "city": "中野",
    "address1": "中野4丁目10-1",
    "address2": "NAKANO CENTRAL PARK EAST"
  }
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/groups/1/venues" POST | jq .

echo 'Create a venue Tacchi'
echo '{
  "venue-name": "Tacchi",
  "address": {
    "postal-code": "150-0002",
    "prefecture": "東京",
    "city": "渋谷",
    "address1": "渋谷２丁目２−１６",
    "address2": ""
  }
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/groups/1/venues" POST | jq .

echo "Get venues"
java -jar ${CLIENT_JAR} "${BASE_URL}/groups/1/venues" GET | jq .
