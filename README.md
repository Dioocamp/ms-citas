# ms-citas

Microservicio de **gestión de pacientes y citas médicas** de la Clínica.
Forma parte de la solución de microservicios de la **Evaluación Parcial 2 (JVY0101).**

> Microservicio relacionado: [`ms-personal-medico`](../ms-personal-medico). `ms-citas` **consume** su API para validar y enriquecer el médico asignado a cada cita.

---

##  Tecnologías

| Componente | Versión |
|---|---|
| Java (JDK) | 17 |
| Spring Boot | 3.3.5 |
| Comunicación entre servicios | `RestClient` (Spring Web) |
| Base de datos | MySQL 8 (por defecto) · H2 (perfil `h2`) |
| Build | Maven (Maven Wrapper) |

##  Arquitectura en capas

```
controller → service (interface + impl) → repository → model (entidades JPA)
                        ↓
                  client (MedicoClient) ── HTTP ──▶ ms-personal-medico
```

| Entidad | Relación |
|---|---|
| `Paciente` | `@OneToMany` → `Cita` |
| `Cita` | `@ManyToOne` → `Paciente` · referencia `medicoId` (de ms-personal-medico) |

---

##  Requisitos previos

- **JDK 17**.
- **MySQL 8** en `localhost:3306` (usuario `root` / clave `root`) — o usar el perfil **H2**.
- **`ms-personal-medico` en ejecución** (puerto 8081) para crear citas (se valida el médico).

> El esquema `clinica_citas` se crea automáticamente.

---

##  Cómo clonar, instalar y ejecutar

```bash
git clone <URL-DEL-REPOSITORIO> ms-citas
cd ms-citas

# Compilar, testear y empaquetar
mvnw.cmd clean package        # Windows
./mvnw clean package          # Linux / Mac

# Ejecutar con MySQL (perfil por defecto)
java -jar target/ms-citas-1.0.0.jar

# Ejecutar con H2 (sin instalar MySQL, con datos de ejemplo)
java -jar target/ms-citas-1.0.0.jar --spring.profiles.active=h2
```

Disponible en **http://localhost:8082**.

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8082/swagger-ui.html |
| Consola H2 (perfil `h2`) | http://localhost:8082/h2-console |

> Consola H2 → JDBC URL: `jdbc:h2:mem:clinica_citas`, usuario `sa`, sin contraseña.

---

## 🔌 Endpoints REST

Base: `http://localhost:8082`

### Pacientes — `/api/pacientes`
| Método | Ruta | Descripción | Éxito |
|---|---|---|---|
| POST | `/api/pacientes` | Crear paciente | 201 |
| GET | `/api/pacientes` | Listar | 200 |
| GET | `/api/pacientes/{id}` | Obtener por id | 200 / 404 |
| PUT | `/api/pacientes/{id}` | Actualizar | 200 / 404 |
| DELETE | `/api/pacientes/{id}` | Eliminar | 204 |

### Citas — `/api/citas`
| Método | Ruta | Descripción | Éxito |
|---|---|---|---|
| POST | `/api/citas` | Agendar (valida médico en ms-personal-medico) | 201 / 409 / 503 |
| GET | `/api/citas` | Listar (`?pacienteId=` o `?medicoId=`) | 200 |
| GET | `/api/citas/{id}` | Obtener por id | 200 / 404 |
| GET | `/api/citas/{id}/detalle` | Cita + datos del médico (otro microservicio) | 200 / 404 |
| PUT | `/api/citas/{id}` | Actualizar | 200 / 404 |
| PATCH | `/api/citas/{id}/estado` | Cambiar estado | 200 / 404 |
| DELETE | `/api/citas/{id}` | Eliminar | 204 |

### Ejemplos `curl`

```bash
# Crear paciente
curl -X POST http://localhost:8082/api/pacientes \
  -H "Content-Type: application/json" \
  -d "{\"rut\":\"99999999-9\",\"nombre\":\"Maria\",\"apellido\":\"Gonzalez\",\"email\":\"maria@mail.cl\",\"telefono\":\"+56911111111\",\"fechaNacimiento\":\"1990-05-12\"}"

# Agendar cita (requiere ms-personal-medico arriba con el medico id=1)
curl -X POST http://localhost:8082/api/citas \
  -H "Content-Type: application/json" \
  -d "{\"fecha\":\"2026-06-10\",\"hora\":\"09:30:00\",\"motivo\":\"Control general\",\"medicoId\":1,\"pacienteId\":1}"

# Ver el detalle enriquecido (datos del medico vienen del otro microservicio)
curl http://localhost:8082/api/citas/1/detalle
```

> Colección lista para importar en [`postman/`](postman/).

---

## Comandos Maven (IE7)

| Comando | Qué hace |
|---|---|
| `mvnw.cmd clean` | Borra `target/` |
| `mvnw.cmd test` | Ejecuta las pruebas |
| `mvnw.cmd package` | Genera el `.jar` ejecutable |
| `mvnw.cmd clean install` | Limpia, testea, empaqueta e instala |

---

##  Ramas Git (IE9)

`main` (estable, tag `v1.0.0`) · `develop` (integración) · `feature/*` (una por funcionalidad).

---

##  Docker y despliegue cloud (EP3)

El microservicio se contenedoriza con un **Dockerfile multi-stage** (build con
Maven + Temurin 17, runtime JRE Alpine, usuario no root, `HEALTHCHECK` contra
`/actuator/health`).

```bash
# Construir y probar en local (perfil H2, sin MySQL)
docker build -t ms-citas .
docker run -d -p 8082:8082 -e SPRING_PROFILES_ACTIVE=h2 ms-citas
curl http://localhost:8082/actuator/health   # {"status":"UP"}
```

**Configuración por variables de entorno:** `SERVER_PORT`, `DB_URL`,
`DB_USERNAME`, `DB_PASSWORD`, `MS_PERSONAL_MEDICO_URL`, `GATEWAY_SECRET`,
`NOTIFICACIONES_ENABLED`, `NOTIFICACIONES_QUEUE_URL`, `AWS_REGION`.

**Integración asíncrona (EP3):** al crear una cita se publica el evento
`CITA_CREADA` en la cola AWS SQS `clinica-citas-queue`; lo consume la Lambda
`clinica-notificador`. Ver el repositorio
[`ep3-infraestructura`](https://github.com/Dioocamp/ep3-infraestructura).

**Pipeline CI/CD** (`.github/workflows/ci-cd.yml`): build + tests con reporte
como artifact → imagen a Docker Hub (tags `latest` y SHA) → despliegue en
Docker Swarm vía SSH. Secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`,
`EC2_HOST`, `EC2_USER`, `EC2_SSH_KEY`.

Despliegue continueo verificado  en la EP3