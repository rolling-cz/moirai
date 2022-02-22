FROM maven:3.8.4-jdk-11 as maven
COPY . /tmp/moirai
WORKDIR /tmp/moirai
RUN mvn package

FROM openjdk:11-jdk-alpine

COPY  --from=maven --chown=nonroot:nonroot /tmp/moirai/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]