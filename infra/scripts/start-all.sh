#!/bin/bash

echo "🚀 Iniciando toda a stack NextMove..."

# 1. Subir infraestrutura via Docker Compose
echo "🔧 Subindo infraestrutura (Docker Compose)..."
cd ../..
docker-compose up -d

echo "✅ Infraestrutura subida com sucesso!"
echo "⏳ Aguardando alguns segundos para garantir que os serviços estejam prontos..."
sleep 5

# 2. Iniciar microserviços
echo "🚀 Iniciando microserviços..."

# Caminhos relativos para cada microserviço
SERVICES=(
  "infra/service-registry"
  "infra/api-gateway"
#  "services/payment-service"
#  "services/notification-service"
)

for SERVICE in "${SERVICES[@]}"
do
  echo "➡️ Iniciando $SERVICE..."
  cd $SERVICE
  mvn spring-boot:run &
  cd - > /dev/null
done

echo "🏁 Ambiente NextMove pronto!"