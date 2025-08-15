docker run --name orders-db \
                              -e POSTGRES_USER=ordersUsername \
                              -e POSTGRES_PASSWORD=ordersPassword \
                              -e POSTGRES_DB=postgres \
                              -p 5434:5432 \
                              -d postgres
docker logs -f orders-db