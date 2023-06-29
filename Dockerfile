FROM openjdk:17

RUN mkdir /server

COPY todoserver.jar /server/todoserver.jar

WORKDIR /server

EXPOSE 3769

CMD ["java", "-jar", "todoserver.jar"]