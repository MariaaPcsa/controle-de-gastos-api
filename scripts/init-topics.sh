#!/bin/bash
# Script to create Kafka topics (run inside an environment with kafka binaries or from container)
set -e

KAFKA_BOOTSTRAP=${KAFKA_BOOTSTRAP:-localhost:9092}

topics=(transaction.created transaction.updated audit.events reports.refresh)
for t in "${topics[@]}"; do
  echo "Creating topic $t"
  kafka-topics.sh --create --topic "$t" --bootstrap-server "$KAFKA_BOOTSTRAP" --partitions 3 --replication-factor 1 || true
done

echo "Topics created"

