#!/bin/bash

SCHEMA_REGISTRY_URL="http://localhost:8081"
SCHEMAS_DIR="./src/main/avro"

wait_for_service() {
  echo "Waiting for $1..."
  until curl -s -f $2 >/dev/null; do
    sleep 5
  done
  echo "$1 is ready!"
}

wait_for_service "Schema Registry" $SCHEMA_REGISTRY_URL

register_schema() {

  local subject=$1
  local schema_file=$2

  echo "Registering schema for $subject from $schema_file..."

  schema=$(jq -c . < "$schema_file" | sed 's/"/\\"/g')

  curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d "{\"schema\": \"$schema\"}" \
    "${SCHEMA_REGISTRY_URL}/subjects/${subject}/versions"

  echo ""
}

cd ..

register_schema "users-value" "${SCHEMAS_DIR}/UserV1.avsc"
register_schema "users-value" "${SCHEMAS_DIR}/UserV2.avsc"

curl -X PUT -H "Content-Type: application/json" \
  http://localhost:8081/config/users-value \
  -d '{"compatibility": "BACKWARD"}'

echo -e "\nAll schemas registered successfully!"
echo "Verify at http://localhost:8081/subjects/"