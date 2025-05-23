#!/bin/bash
echo "⛔ Parando todos os microserviços e containers..."

# Para os containers
cd ../..
docker-compose down

# Para os processos Java (Spring Boot)
pkill -f 'spring-boot:run'

echo "✅ Tudo parado!"