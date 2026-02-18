FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Crear usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR desde la ruta donde se genera en el proyecto
# Este Dockerfile debe construirse desde la raíz del proyecto (api-franquicias)
# Ejemplo: docker build -f deployment/Dockerfile -t franchise-api .
COPY applications/app-service/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
