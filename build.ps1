# 1) Limpar builds antigos
if (Test-Path build) { Remove-Item -Recurse -Force build }
New-Item -ItemType Directory -Force build/classes

# 2) Compilar os .java usando as libs do projeto
# No Windows, separamos o Classpath com ponto e vírgula (;)
$classpath = "lib/flatlaf-3.7.1.jar;lib/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar;lib/com.miglayout.swing_11.4.2.jar;lib/com.miglayout.core_11.4.2.jar"
$javaFiles = Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -cp $classpath -d build/classes $javaFiles

# 3) Montar o "fat jar": extrair as libs dependentes dentro de uma pasta única
New-Item -ItemType Directory -Force build/fatjar
Set-Location build/fatjar

jar xf ../../lib/flatlaf-3.7.1.jar
jar xf ../../lib/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar
jar xf ../../lib/com.miglayout.swing_11.4.2.jar
jar xf ../../lib/com.miglayout.core_11.4.2.jar

# 4) IMPORTANTE: guardar o arquivo de registro do driver MySQL ANTES de mexer no META-INF
New-Item -ItemType Directory -Force ../driverbackup
Copy-Item META-INF/services/java.sql.Driver ../driverbackup/

# 5) Remover os META-INF das libs (manifests conflitantes) e colocar as classes compiladas
Remove-Item -Recurse -Force META-INF
Copy-Item -Recurse -Force ../classes/* .

# 6) Restaurar o arquivo do driver MySQL
New-Item -ItemType Directory -Force META-INF/services
Copy-Item ../driverbackup/java.sql.Driver META-INF/services/java.sql.Driver

# 7) Copiar as imagens para dentro da estrutura do jar
New-Item -ItemType Directory -Force img
Copy-Item ../../img/*.png, ../../img/*.jpg, ../../img/*.jpeg img/ -ErrorAction SilentlyContinue

Set-Location ../..

# 8) Criar o manifest apontando a classe principal
$manifestContent = @"
Manifest-Version: 1.0
Main-Class: Main

"@
Set-Content -Path build/MANIFEST.MF -Value $manifestContent

# 9) Gerar o jar final
Set-Location build
jar cfm SistemaDelivery.jar MANIFEST.MF -C fatjar .
Set-Location ..

# 10) Rodar
java -jar build/SistemaDelivery.jar