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

echo 'Shinichi joins clj-nakano'
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

echo 'Create a meetup situated-program-challenge #1'
echo '{
	"title": "situated-program-challenge #1",
	"start-at": "2017-12-12T09:30:30.597Z",
	"end-at": "2017-12-12T11:30:30.597Z",
	"venue-id": 1
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/groups/1/meetups" POST | jq .

echo 'Shinichi joins situated-program-challenge #1'
java -jar ${CLIENT_JAR} "${BASE_URL}/members/1/meetups/1" POST | jq .

echo 'Kenji joins situated-program-challenge #1'
java -jar ${CLIENT_JAR} "${BASE_URL}/members/2/meetups/1" POST | jq .

echo 'Create a meetup situated-program-challenge #2'
echo '{
	"title": "situated-program-challenge #2",
	"start-at": "2018-01-16T09:30:30.597Z",
	"end-at": "2018-01-16T11:30:30.597Z",
	"venue-id": 1
}' | java -jar ${CLIENT_JAR} "${BASE_URL}/groups/1/meetups" POST | jq .

echo 'Kenji joins situated-program-challenge #2'
java -jar ${CLIENT_JAR} "${BASE_URL}/members/2/meetups/2" POST | jq .

echo "Get meetups"
java -jar ${CLIENT_JAR} "${BASE_URL}/groups/1/meetups" GET | jq .
