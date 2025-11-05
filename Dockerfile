FROM maven:3.9-ibm-semeru-21-jammy AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre AS builder
WORKDIR /builder

ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre
WORKDIR /application

# 拷贝 Spring Boot 分层
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

ENTRYPOINT ["java", "-jar", "application.jar"]