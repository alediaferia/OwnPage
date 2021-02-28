#!/bin/bash
 
echo "Registering initial user..."

curl -v \
  -H"Content-Type: application/json" \
  -H"Accept: application/json" \
  -u "owner:$OWNER_SETUPPASSWORD" \
  -d "{\"name\": \"admin\", \"password\": \"$ADMIN_PASSWORD\"}" localhost:$PORT/api/users/admin
