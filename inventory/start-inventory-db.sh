docker run --name inventory-db \
  -e POSTGRES_USER=inventoryUser \
  -e POSTGRES_PASSWORD=inventoryPassword \
  -e POSTGRES_DB=postgres \
  -p 5433:5432 \
  -d postgres

docker logs -f inventory-db