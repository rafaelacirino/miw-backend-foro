# [Máster en Ingeniería Web por la Universidad Politécnica de Madrid (miw-upm)](http://miw.etsisi.upm.es)
## Back-end con Tecnologías de Código Abierto (BETCA).
> Aplicación Capturing Foro. Pretende ser un ejemplo práctico y real de todos los conocimientos vistos

## Estado del código
[![Spring User - Tests](https://github.com/rafaelacirino/miw-backend-foro/actions/workflows/ci.yml/badge.svg)](https://github.com/rafaelacirino/miw-backend-foro/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rafaelacirino_miw-backend-foro&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=rafaelacirino_miw-backend-foro)

## Tecnologías utilizadas:
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.3/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.3/reference/web/servlet.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.3/reference/web/spring-security.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Docker](https://docs.docker.com/manuals/)
* [Sonal Cloud](https://docs.sonarsource.com/sonarqube-cloud/)
* [PostgreSQL](https://www.postgresql.org/docs/current/)


### Ejecución en Local:
1. Arrancar Docker Desktop;
2. Ejecutar en consola: `docker compose up --build -d`
3. Arrancar la consola de PostgreSQL sobre la BD: `docker exec -it postgres-db psql -U postgres -d foro`
4. Arrancar el proyecto desde la consola: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

### Documentación de ForoAPI
* Cliente Web (OpenAPI): `http://localhost:8081/swagger-ui.html`
* Para hacer el build del proyecto desde la consola: `mvn clean install`
* Para parar: `docker compose stop`, aunque resulta mas práctico manejar los contenedores desde _Docker Desktop_