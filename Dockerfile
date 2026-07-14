# ============================================================
#  Etapa 1: BUILD - compila el microservicio con Maven + JDK 17
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Primero solo el pom.xml: las dependencias quedan cacheadas en su
# propia capa y no se vuelven a descargar si solo cambia el codigo.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Los tests corren en la etapa build-test del pipeline CI/CD.
RUN mvn -B clean package -DskipTests

# ============================================================
#  Etapa 2: RUNTIME - imagen liviana solo con el JRE y el JAR
# ============================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario sin privilegios: el contenedor nunca corre como root.
RUN addgroup -S clinica && adduser -S -G clinica clinica
USER clinica

COPY --from=build /workspace/target/ms-citas-*.jar app.jar

EXPOSE 8082

# Docker/Swarm consideran sano el contenedor solo si Actuator responde.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD wget -q -T 5 -O /dev/null http://localhost:8082/actuator/health || exit 1

# MaxRAMPercentage respeta el limite de memoria que imponga el orquestador.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
