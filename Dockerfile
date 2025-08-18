# -------- Build stage --------
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app
# Copy pom and download deps first for better caching
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
# Copy sources and build
COPY src ./src
RUN mvn -B -q -DskipTests package

# -------- Run stage --------
FROM eclipse-temurin:8-jre
WORKDIR /app
# Tweak memory for small free tiers
ENV JAVA_OPTS="-Xms256m -Xmx512m"
# Copy the fat jar from build stage (assumes single jar in target)
COPY --from=build /app/target/*.jar /app/app.jar
# Honor platform provided PORT (Render/Koyeb) or fallback to 8080
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
