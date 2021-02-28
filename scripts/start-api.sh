#!/bin/bash

JWT_DIR=/opt/jwt
mkdir -p $JWT_DIR
if [ ! -f $JWT_DIR/private.pem ]; then
    echo "Generating JWT keypair..."
    openssl genrsa  -out $JWT_DIR/private.pem 2048
    openssl rsa -in $JWT_DIR/private.pem -pubout > $JWT_DIR/key.pub
    echo "Generated JWT keypair."
fi;

echo "JWT public key:"
cat $JWT_DIR/key.pub

echo "Starting API Server..."
java $JAVA_OPTS -jar /home/ownuser/core-0.0.1-SNAPSHOT.jar
