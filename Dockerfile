FROM openjdk:14
COPY ./ /myapp/
WORKDIR /myapp/
ENV CLASSPATH=.:antlr-4.9.3-complete.jar
RUN javac -cp src/ src/LabTest.java -d dst/
