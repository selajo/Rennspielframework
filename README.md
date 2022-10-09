# Rennspielframework

# Maven-Anleitung:
Build und Jar-File erstellen:
- mvn package
- mvn clean package (löscht altes jar-File --> sauberer)

# Jar-File ausführen:

Server:
- java -cp target/Rennspielframework-0.0.1-SNAPSHOT.jar anwendungsschicht.RennspielStart server "/Res/Config/ConfigWorld00.json"	#Kompilieren -> Testing -> Packaging
- mvn clean package exec:java		#Kompilieren -> Testing -> Packaging -> Executing
 

Client:
- java -cp target/Rennspielframework-0.0.1-SNAPSHOT.jar anwendungsschicht.RennspielStart client 8000 localhost Mensch

KI-Clients:

trivial:
- java -cp target/Rennspielframework-0.0.1-SNAPSHOT.jar anwendungsschicht.RennspielStart client 8000 localhost KI trivial

astern:
- java -cp target/Rennspielframework-0.0.1-SNAPSHOT.jar anwendungsschicht.RennspielStart client 8000 localhost KI astern

Entwickler-Team:
- Lanzl Josef
- Meixner Simon
- Zimmer André
