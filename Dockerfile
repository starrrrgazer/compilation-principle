FROM openjdk:15
COPY . /app/
WORKDIR /app/

RUN java -jar antlr-4.9.3-complete.jar -visitor MiniSys.g4

RUN javac -cp antlr-4.9.3-complete.jar *.java -d dst/
