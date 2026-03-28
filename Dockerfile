## Base image
#FROM eclipse-temurin:21-jdk-alpine
#
## Jar faylı konteynerə əlavə et
#COPY target/dinereserve-auth-service-0.0.1-SNAPSHOT.jar app.jar
#
## Konteyner içində run et
#ENTRYPOINT ["java","-jar","/app.jar"]
#
## Port expose
#EXPOSE 8080