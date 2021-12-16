FROM openjdk:15
WORKDIR /app/
COPY *.java ./
COPY *.jar ./
ENV CLASSPATH=.:antlr-4.9.3-complete.jar
RUN javac -d ./ *.java
