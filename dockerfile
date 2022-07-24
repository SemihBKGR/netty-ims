FROM gradle:7.4.1-jdk17-alpine AS build
COPY ./. ./netty-ims
WORKDIR ./netty-ims
RUN gradle test
RUN gradle jar
RUN mv ./build/libs/netty-ims-1.0.0.jar /

FROM openjdk:17-alpine
COPY --from=build /netty-ims-1.0.0.jar ./netty-ims/netty-ims-1.0.0.jar
ENTRYPOINT ["java","-jar","/netty-ims/netty-ims-1.0.0.jar"]
