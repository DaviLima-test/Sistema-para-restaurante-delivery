# 1. Mudamos para o JDK 11 (versão exata do seu projeto)
FROM eclipse-temurin:11-jdk-alpine

LABEL authors="arthurmartins"

# 2. Define a pasta de trabalho dentro do container
WORKDIR /app

# 3. Copia a pasta de código-fonte
COPY src ./src

# 4. Copia as dependências (.jar) da raiz para dentro do container
COPY lib/com.miglayout.core_11.4.2.jar .
COPY lib/com.miglayout.swing_11.4.2.jar .
COPY lib/flatlaf-3.7.1.jar .
COPY lib/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar ./mysql-connector-j-8.0.33.jar

# 5. Compila o projeto incluindo as bibliotecas no Classpath (-cp)
RUN find src -name "*.java" > sources.txt && \
    javac -cp ".:com.miglayout.core_11.4.2.jar:com.miglayout.swing_11.4.2.jar:flatlaf-3.7.1.jar:mysql-connector-j-8.0.33.jar" -d out @sources.txt

# 6. Vai para a pasta onde os arquivos compilados foram gerados
WORKDIR /app/out

# 7. Executa a classe Main trazendo as dependências junto
ENTRYPOINT ["java", "-cp", ".:../com.miglayout.core_11.4.2.jar:../com.miglayout.swing_11.4.2.jar:../flatlaf-3.7.1.jar:../mysql-connector-j-8.0.33.jar", "Main"]