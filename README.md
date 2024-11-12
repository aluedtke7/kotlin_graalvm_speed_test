# quarkus-kotlin-rest-native
Beispiel Rest Service mit Quarkus und Kotlin

## Voraussetzungen
Um alle aufgeführten Beispiele ausführen zu können, müssen die folgenden Pakete/Tools installiert sein:

- Java SDK 17 oder neuer
- Ein Text-Editor oder eine IDE
- `curl` oder Postman
- Docker oder Podman o.ä.

## Ausführen
Um das Programm im Development Modus zu starten, gibt man Folgendes ein:

    ./mvnw compile quarkus:dev

Hiermit wird das Programm übersetzt und ausgeführt. Bei Änderungen am Source Code wird es automatisch neu 
kompiliert, sobald es nötig ist. Bei einem Rest Service ist dies der Fall, wenn ein neuer Request (nach den Änderungen und Speicherung) eingeht. Der Dev Modus lässt sich mit der Taste `q` beenden.

> **_Hinweis:_** Quarkus hat ein Development UI, welches unter http://localhost:8080/q/dev/ erreichbar ist.

Jetzt kann der Rest Service in einer anderen Shell mit `curl` getestet werden:

    curl -X POST http://localhost:8080/login -H 'Content-Type: application/json' -d '{"username":"andi","password":"geheim"}'

Als Ergebnis wird ein Token im Json Format zurückgeliefert.

## Kompilierung (Uber-Jar)
Um ein Uber-Jar zu erhalten, gibt man Folgendes ein:

    ./mvnw package -Dquarkus.package.type=uber-jar

Hiermit wird die Datei `quarkus-kotlin-1.0.0-SNAPSHOT-runner.jar` im `target` Verzeichnis erzeugt. Das Programm kann mittels

    java -jar target/quarkus-kotlin-1.0.0-SNAPSHOT-runner.jar

gestartet werden.

## Kompilierung (nativ)
Um ein natives Binary zu erhalten, gibt man Folgendes ein:

    ./mvnw package -Pnative -Dquarkus.native.container-build=true

Hierdurch wird das Programm mithilfe der GraalVM in einem Docker Container kompiliert. Als Ergebnis
wird die Datei `quarkus-kotlin-1.0.0-SNAPSHOT-runner` im `target` Verzeichnis erzeugt. Das Programm kann mittels

    target/quarkus-kotlin-1.0.0-SNAPSHOT-runner

gestartet werden.

## Ein Docker Image erzeugen
Mit dem folgenden Befehl wird ein Docker Image Namens `quarkus-kotlin-rest-native` in Version (Tag) 1.0.0 
erzeugt und im lokalen Docker Repository abgelegt.

    docker build -f src/main/docker/Dockerfile.native-micro -t quarkus-kotlin-rest-native:1.0.0 .

hierbei wurde das **micro** Basis-Image verwendet. Soll das *normale* Basis-Image verwendet werden, so
gibt man Folgendes ein:

    docker build -f src/main/docker/Dockerfile.native -t quarkus-kotlin-rest-native:1.0.1 .

Die Größe der erzeugten Images kann wie folgt angezeigt werden:

    docker image ls

ergibt

    REPOSITORY                   TAG               IMAGE ID       CREATED         SIZE
    quarkus-kotlin-rest-native   1.0.1             1cc4c51a9036   5 seconds ago   149MB
    quarkus-kotlin-rest-native   1.0.0             c18818175b6b   2 minutes ago   84.8MB

## Docker Container starten und testen
Ein Container mit dem soeben erzeugten Image starten:

    docker run --rm -p 8080:8080 quarkus-kotlin-rest-native:1.0.0   # oder
    docker run --rm -p 8080:8080 quarkus-kotlin-rest-native:1.0.1

und mit dem gleichen `curl` Befehl in einer anderen Shell testen:

    curl -X POST http://localhost:8080/login -H 'Content-Type: application/json' -d '{"username":"andi","password":"geheim"}'

Auch hier sollte ein Token im Json Format zurückgeliefert werden. Das Token wird aber selbst bei 
gleichen Eingabedaten einen anderen Wert haben. Dies liegt in der Natur von JWTs.

## JWT überprüfen
Auf der Webseite https://jwt.io/ kann das erzeugte Token dekodiert und verifiziert werden. Hierzu einfach 
den Inhalt von `jwt` in das Feld **Encoded** eingeben. Unter **Decoded** werden dann die Informationen aus dem Header 
und die eigentliche Payload ausgegeben.

# Laufzeit Tests
## Test für Computer Speed Messung
Um die Geschwindigkeit des Computers einordnen zu können, sollte der folgende Befehl zweimal ausgeführt werden.
Bei der ersten Ausführung werden üblicherweise die Docker Images heruntergeladen und gehen somit mit in die
Zeitmessung ein. Bei der zweiten Ausführung wird dann die eigentlich gewünschte Zeit für das reine Kompilieren
ausgegeben. Evtl. kann auch ein dritter Lauf ein etwas besseres Ergebnis liefern.

In der untenstehenden Tabelle sind die Zeiten eingetragen, die von Maven unterhalb von **BUILD SUCCESS**
ausgegeben werden.

Kompilierung mit Podman:

    ./mvnw clean package -Pnative -Dquarkus.native.container-build=true  -Dquarkus.native.container-runtime=podman

Kompilierung mit Docker:

    ./mvnw clean package -Pnative -Dquarkus.native.container-build=true  -Dquarkus.native.container-runtime=docker

-----------------------------------------------------------------------------------

|                Computer |               Typ | RAM in GB | CPUs | Dauer mit Docker in s | Dauer mit Podman in s | Anmerkung |
|------------------------:|------------------:|----------:|-----:|----------------------:|----------------------:|----------:|
|     Apple MacBooPro 16" |             M4max |        48 |   16 |                       |                  35.3 |           |
|     Apple MacBooPro 16" |             M3max |        64 |      |                       |                       |           |
|     Apple MacBooPro 14" |             M2max |        64 |   12 |                  50.2 |                  50.2 |         1 |
|     Apple MacBooPro 16" |             M1max |        32 |   10 |                       |                    63 |           |
| Lenovo Thinkpad T14gen2 | Ryzen 5 pro 5650U |        16 |   12 |                    92 |                       |         2 |
|    Lenovo ThinkPad T460 |          i7-6600U |        16 |    4 |                   220 |                       |         2 |
|      HP EliteBook 8470p |          i7-3540M |        16 |    4 |                   248 |                       |         3 |

-----------------------------------------------------------------------------------

> Bei Podman ist darauf zu achten, dass der Podman Machine genügend CPUs (am besten alle) und genügend RAM
> (mindestens 8 GB) zugewiesen werden
> 

Anmerkungen
1. Verwendet wurde Docker Desktop v4.35.1, Podman Desktop v1.14 und Podman v5.2.0
2. Verwendet wurde Docker auf Manjaro/Linux
3. Verwendet wurde Docker auf Arch/Linux

