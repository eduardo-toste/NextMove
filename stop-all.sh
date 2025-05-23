#!/bin/bash
echo "⛔ Parando todos os microserviços e containers..."

# Para os containers
cd infra
docker-compose down
cd ..

# Para os processos Java (Spring Boot)
pkill -f 'spring-boot:run'

echo "✅ Tudo parado!"