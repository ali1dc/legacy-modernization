FROM openjdk:8

EXPOSE 8093

USER nobody

COPY shipment-0.0.1-SNAPSHOT.jar /opt/shipment.jar

ENTRYPOINT ["java","-jar","/opt/shipment.jar"]