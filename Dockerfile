FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom files
COPY pom.xml .
COPY agent-core/pom.xml agent-core/
COPY agent-api/pom.xml agent-api/
COPY agent-worker/pom.xml agent-worker/
COPY agent-web/pom.xml agent-web/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY agent-core/src agent-core/src
COPY agent-api/src agent-api/src
COPY agent-worker/src agent-worker/src
COPY agent-web/src agent-web/src

# Build
RUN mvn clean package -DskipTests -B

# Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy jar from builder
COPY --from=builder /app/agent-web/target/agent-web-*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
