FROM eclipse-temurin:21

LABEL maintainer  ="ahmednsra329@gmail.com"

WORKDIR /app

COPY target/Eduko-0.0.1-SNAPSHOT.jar /app/target/Eduko.jar

ENTRYPOINT ["java", "-jar","/app/target/Eduko.jar"]