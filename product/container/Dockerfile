FROM openjdk:8

EXPOSE 8091

USER nobody

COPY product-0.0.1-SNAPSHOT.jar /opt/product.jar

ENTRYPOINT ["java","-jar","/opt/product.jar"]
