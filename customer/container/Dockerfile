FROM openjdk:8

EXPOSE 8093

USER nobody

COPY customer-0.0.1-SNAPSHOT.jar /opt/customer.jar

ENTRYPOINT ["java","-jar","/opt/customer.jar"]