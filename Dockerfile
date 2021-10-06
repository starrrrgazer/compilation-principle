FROM openjdk:15
WORKDIR /myapp/
COPY ./* /myapp/
RUN javac -cp src/ src/Lexer.java -d dst/
