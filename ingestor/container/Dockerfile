FROM openjdk:8
ARG JAR_FILE="build/libs/*.jar"

EXPOSE 8092

USER nobody

COPY ingestor-0.0.1-SNAPSHOT.jar /opt/ingestor.jar

ENTRYPOINT ["java","-jar","/opt/ingestor.jar"]
