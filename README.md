# Spring Boot + PostgreSQL Dockerized

### Creating a docker network
``` bash
docker network create ptk_customers
``` 
### Postgresql docker setup
``` bash
#Build PostfresSQL image (From the position of the Dockerfile)
docker build . -t postgres_db

#Run Postgres container from image
docker run --name my_database --network my_network postgres_db
```

### Spring Boot docker setup
``` bash
#Build Spring boot image (From the position of the Dockerfile)
docker build . -t ptk_customers_service

#Run backend service and hos tit on localhost port 8080
docker run -d -p 8081:8080 --name ptk_customers_backend --network my_network ptk_customers_service

```