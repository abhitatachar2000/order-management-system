docker run --name catalog-db \
  -e POSTGRES_USER=catalogUser \
  -e POSTGRES_PASSWORD=catalogPassword \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 \
  -d postgres

docker logs -f catalog-db