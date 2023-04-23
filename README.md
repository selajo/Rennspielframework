# BA-Rennspielframework-AndreZimmer-202122

# Maven-Anleitung:
Build und Jar-File erstellen:
- `mvn package`
- `mvn clean package (löscht altes jar-File --> sauberer)`

# Jar-File ausführen:

Übergabeparameter:
- -a,--auto <arg>        Client: Autoauswahl (1: rot, 2: blau, 3: gruen, 4:
                      	gelb)
- -c,--config <arg>      Server: Pfad zur Konfiguraationsdatei fuer den
                        Server
- -headless,--headless   Server: Started keine grafische Ansicht
- -i,--ip <arg>          Client: IP zur Verbindung mit dem Server
- -in,--instanz <arg>    Angabe, ob Server oder Client
- -ki,--ki <arg>         Client: Spieler ist eine KI
- -m,--mensch            Client: Spieler ist ein Mensch
- -p,--port <arg>        Client: Port zur Verbindung mit dem Server
- -r,--replay <arg>      Client: Es wird ein Replay ausgefuehrt

Server:
- `java -jar .\target\Rennspielframework-0.0.1-SNAPSHOT.jar -in server -c /Res/Config/ConfigWorld00.json`
 

Client (Mensch):
- `java -jar .\target\Rennspielframework-0.0.1-SNAPSHOT.jar -in client -p 8000 -i localhost -m`

KI-Clients:

trivial:
- `java -jar .\target\Rennspielframework-0.0.1-SNAPSHOT.jar -in client -p 8000 -i localhost -ki trivial`

astern:
- `java -jar .\target\Rennspielframework-0.0.1-SNAPSHOT.jar -in client -p 8000 -i localhost -ki astern`

dijkstra:
- `java -jar .\target\Rennspielframework-0.0.1-SNAPSHOT.jar -in client -p 8000 -i localhost -ki dijkstra`


Replay:
- `java -jar .\target\Rennspielframework-0.0.1-SNAPSHOT.jar -in client -p 8000 -i localhost -r <config>`


-----
Map-Editor:
- `java -cp target/Map-Editor-1.0-SNAPSHOT.jar com/mapeditor/view/App`
