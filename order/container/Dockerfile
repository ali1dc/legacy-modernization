FROM openjdk:8

EXPOSE 8094

USER nobody

COPY order-0.0.1-SNAPSHOT.jar /opt/order.jar

ENTRYPOINT ["java","-jar","/opt/order.jar"]
