# Code challenge - Ejercicio Lvl 2: Microservicio Metrics

Este proyecto es un microservicio **Java** utilizando **Spring Boot** y **WebFlux** para construir una API reactiva de captura y métricas de tiempo.

---

## 🚀 Configuración del Proyecto

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/gpalacios26/metrics-ms.git
cd metrics-ms
```

### 2️⃣ Ejecutar la aplicación

Con **Maven**:

```bash
mvn spring-boot:run
```

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Especificación OpenAPI (en JSON): [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🌐 Endpoints de la API

Utilizar la colección `Metrics-MS.postman_collection.json` que se encuentra dentro de la carpeta postman.

---

## 🧪 Ejecución de Pruebas

Para correr las pruebas unitarias:

Con **Maven**:

```bash
mvn clean test
```

Las pruebas utilizan `JUnit 5`, `Mockito` y `WebTestClient` para verificar la funcionalidad de la API.

El reporte de cobertura de pruebas se genera con `Jacoco` en la carpeta `target/site/jacoco/index.html`.

---

## 📑 Requisitos

- **Java 17** instalado.
- **Maven** configurado.
- IDE recomendado: **IntelliJ IDEA**.
