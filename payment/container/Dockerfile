FROM openjdk:8

EXPOSE 8095

USER nobody

COPY payment-0.0.1-SNAPSHOT.jar /opt/payment.jar

ENTRYPOINT ["java","-jar","/opt/payment.jar"]
