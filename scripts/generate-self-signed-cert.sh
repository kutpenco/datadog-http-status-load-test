#!/usr/bin/env bash
set -euo pipefail
mkdir -p /certs
if [[ ! -f /certs/keystore.p12 ]]; then
  keytool -genkeypair -noprompt \
    -alias localhost -keyalg RSA -keysize 2048 -validity 3650 \
    -storetype PKCS12 -keystore /certs/keystore.p12 \
    -storepass changeit -keypass changeit \
    -dname "CN=localhost, OU=Datadog Load Test, O=kutpenco, C=BR" \
    -ext "SAN=DNS:localhost,IP:127.0.0.1"
fi
