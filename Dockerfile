FROM openjdk:15
COPY ./* /myapp/
WORKDIR /myapp/
RUN javac -cp src/ src/Lexer.java -d dst/
