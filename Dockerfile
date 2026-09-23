FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN mkdir -p /certs
COPY --from=build /workspace/target/*.jar /app/app.jar
COPY scripts/generate-self-signed-cert.sh /usr/local/bin/generate-self-signed-cert.sh
RUN chmod +x /usr/local/bin/generate-self-signed-cert.sh
EXPOSE 8080 8443
ENTRYPOINT ["/bin/bash", "-c", "/usr/local/bin/generate-self-signed-cert.sh && exec java ${JAVA_OPTS:-} -XX:+UseContainerSupport -XX:ActiveProcessorCount=2 -Xms512m -Xmx1536m -jar /app/app.jar"]
