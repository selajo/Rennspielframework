package anwendungsschicht;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import spiellogikschicht.KartenManager;
import spiellogikschicht.Spielakteur;
import spiellogikschicht.Spielstadien;

/**
 * Klasse die den Netzwerkablauf kuemmert, kann sowohl Client oder Server darstellen.
 * @author André
 * Implementiert EventListner sodass Ereingisse empfangen werden koennen
 */
public class Netzwerkverbindung implements EventListener {
	

	SpielApplikation app;

	EventManager event;
	
	Spieloptionen optionen;
	
	/**
	 * HashMap die alle ausfuehrbaren Funktionen enthalten die von eingehenden Nachrichten empfangen werden
	 */
	public final HashMap<Integer, Executable> idMethods = new HashMap<Integer, Executable>();
	
	/**
	 * ServerSocket
	 */
	public ServerSocket server;
	/**
	 * Port der Neztwerkverbindung
	 */
	public int port;
	/**
	 * Liste mit den technischen Daten der Clients enthaelt Instanzen von RemoteClient
	 */
	public ArrayList<RemoteClient> clients;
	/**
	 * Liste mit den technischen Daten der Clients, die geloescht werden sollen
	 */
	public ArrayList<RemoteClient> toBeDeleted;
	/**
	 * Thead der offen ist fuer eingehende Clientanfragen
	 */
	public Thread listeningThread;
	/**
	 * Gibt an ob der Netzwerkthread gestoppt wurde
	 */
	public boolean stopped;
	/**
	 * Boolean Paramter der gesetzt wird ob man die DebugNachrichten erhalten moechte
	 */
	public boolean muted = true; //debug Eigenschaften
	/**
	 * gibt das Intervall an in dem die Netzwerkverbindung ueberprueft wird
	 */
	public long pingInterval = 30 * 1000; // 30 seconds
	/**
	 * Paramter fuer die Login_ID = 1
	 */
	public final int LOGIN_ID = 1;
	
	//ClientVariablen
	/**
	 * ClientID
	 */
	public int id = 0;
	/**
	 * Client Login Socket
	 */
	public Socket loginSocket;
	/**
	 * Client InetNetAddress
	 */
	public InetSocketAddress address;
	/**
	 * Client Timeout
	 */
	public int timeout;
	/**
	 * Hostname des Servers
	 */
	String hostname;
	/**
	 * Zaehlen der Fehlermeldungen
	 */
	public int errorCount;
	
	
	/**
	 * Konsturktor durch den mitgelieferten Paramter entscheidet ob als Server oder Client initialisert werden soll
	 * @param verbindungstyp Client/Server
	 * @param app SpielApplikation
	 */
	public Netzwerkverbindung(String verbindungstyp, SpielApplikation app) {
		event = EventManager.getInstance();
		
		this.app = app;
		optionen = Spieloptionen.getInstance();
		if(verbindungstyp == "server") {
			setServerKonstruktor();
		}
		
		else if(verbindungstyp == "client") {
			setClientKonstruktor();
		}
	}
	
	
	/**
	 * Client Konstruktor, Initialisiere die Klasse als Client
	 */
	private void setClientKonstruktor() {
		event.subscribe("key_event", this);
		
		this.errorCount = 0;
		this.address = new InetSocketAddress(optionen.hostname, optionen.port);
		this.timeout = optionen.timeout;
		
		ClientPreStart();
		
		ClientStart();	
		
	}

	/**
	 * Registierung aller Methoden von den Einkommenden Datenpackete
	 */
	private void ClientPreStart() {
		
		//Empfange Client ID 
		registerMethod(2, new Executable() {
			@Override
			public void run(Datapackage msg, Socket socket) {
				//Neue Client ID zugewie�en
				int clientID = convertByteArrayToInt(msg.data);
				id = clientID;
				System.out.println("Client: hat Nachricht 2 erhalen, antwort mit zugewiesener ID" + id);
				Spieloptionen optionen = Spieloptionen.getInstance();
				optionen.spielerID = id;
				optionen.starteClientAnsicht++;
			}
			});
		
		//Empfange Liste der Autos, Sende Ausgewaehlten Autotyp zurueck
		registerMethod(3, new Executable() {
			@Override
			public void run(Datapackage msg, Socket socket) {
				//Neue Client ID zugewie�en
				
				System.out.println("Client: hat Nachricht 3 erhalen, uebertrage liste autos");
				
				int length = msg.datalength;
				byte[] daten = msg.getByteMessage();
				
				Map <Integer,Object[]> autoListe = new HashMap<Integer,Object[]>();
				byte[]hilfs = new byte[4];
				
				for(int j = 0; j<4; j++) {
					hilfs[j] = daten[j+8];
				}
				int anzahlAutos1 = convertByteArrayToInt(hilfs);
				
				
				int it = anzahlAutos1;
				for(int i = 0; i < it; i++) {
					//GetAutoId
					for(int j = 0; j<4; j++) {
						hilfs[j] = daten[16*i+j+12]; //Plus 12 Bytes, sind der Header
					}
					int autoID = convertByteArrayToInt(hilfs);
					//GetMasse
					for(int j = 0; j<4; j++) {
						hilfs[j] = daten[16*i+j+4+12];
					}
					int masse = convertByteArrayToInt(hilfs);
					//GetmaxGeschwindigkeit
					for(int j = 0; j<4; j++) {
						hilfs[j] = daten[16*i+j+8+12];
					}
					int maxGeschwindigkeit = convertByteArrayToInt(hilfs);
					//GetMasse
					for(int j = 0; j<4; j++) {
						hilfs[j] = daten[16*i+j+12+12];
					}
					int fahrkeraftsteigerung = convertByteArrayToInt(hilfs);
					Object[] o = new Object[] {autoID, masse, maxGeschwindigkeit, fahrkeraftsteigerung};
					autoListe.put(autoID, o);	
				}
				
				System.out.println("Bitte waehlen Sie ein Auto aus: ");
				for (Map.Entry<Integer,Object[]> entry : autoListe.entrySet()) {
					Object[] data = entry.getValue();
					System.out.println("Auto: " + (int)data[0] + " Masse: " + (int)data[1] + " Hoechstgeschwindigkeit (Newton): " + (int)data[2] + " Beschleunigung: " + (int)data[3]);
					
			    }
				
				optionen.starteClientAnsicht++; //Empfangen so dass man die Ansicht starten kann
				
				//Einlesen Daten
				Scanner sc = new Scanner(System.in);
				int intEingabe = 0;
				while(true) {
					String eingabe = sc.next();
					intEingabe = Integer.parseInt(eingabe);
					if(autoListe.containsKey(intEingabe)) {
						sc.close();
						break;
					}
					else{
						System.out.println(eingabe + " entspricht nicht der ID eines Autos, bitte waehlen Sie eine aus");
					}
				}
				
				//Versenden Daten
				Datapackage pack = new Datapackage();
				int packID = 4;
				length = 0;
				
				byte[] spielerIDbyte = convertIntToByteArray(id);
				length +=4;
				
				byte [] eingabe = convertIntToByteArray(intEingabe);
				length += 4;
				
				byte[] allByteArray = new byte[spielerIDbyte.length + eingabe.length];

				ByteBuffer buff = ByteBuffer.wrap(allByteArray);
				buff.put(spielerIDbyte);
				buff.put(eingabe);

				byte[] combined = buff.array();
				
				
				
				pack.setDatenDatapackage(packID, length, combined);
				
				ClientSendMessage(pack, 10000);
				
				System.out.println("Client: hat Nachricht 4 gesendet");
			}
			});
		
				
		//Empfange update_koordinate 
		registerMethod(5, new Executable() {
			@Override
			public void run(Datapackage msg, Socket socket) {
				//Log("Client: hat Nachricht 5 erhalen, update koordinate");
				int akteurid;
				int akteurdirection;
				int akteurpositionX;
				int akteurpositionY;
				
				byte[]b = msg.data;
				byte[]hilfs = new byte[4];
				for(int i = 0; i<4; i++) {
					hilfs[i] = b[i];
				}
				akteurid = new BigInteger(hilfs).intValue();
				
				for(int i = 0; i<4; i++) {
					hilfs[i] = b[4+i];
				}
				akteurdirection = new BigInteger(hilfs).intValue();
				String direction = "";
				if(akteurdirection == 1) {
					direction = "up";
				}
				else if(akteurdirection == 2) {
					direction = "down";
				}
				else if(akteurdirection == 3) {
					direction = "left";
				}
				else if(akteurdirection == 4) {
					direction = "right";
				}
				
				for(int i = 0; i< 4; i++) {
					hilfs[i] = b[8+i];
				}
				akteurpositionX = new BigInteger(hilfs).intValue();
				
				for(int i = 0; i< 4; i++) {
					hilfs[i] = b[12+i];
				}
				akteurpositionY = new BigInteger(hilfs).intValue();

				//Schicke Event
				event.notify("update_koordinate", akteurid, direction, akteurpositionX, akteurpositionY);
				}
			});
		
		//Empfange die Spielkarte
		registerMethod(7, new Executable() {
			@Override
			public void run(Datapackage msg, Socket socket) {
				System.out.println("Client: hat Nachricht 7 erhalen, sende Karte");
				
				int karteArray[][] = new int [optionen.maxBildschirmSpalten][optionen.maxBildschirmZeilen];
				byte daten [] = msg.data;
				byte hilfs[] = new byte[4];
				int intHilf = 0;
				for(int i = 0; i < optionen.maxBildschirmZeilen; i++) {
					for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
						for(int k = 0;k<4; k++) {
							hilfs[k] = daten[intHilf + k];
						}
						intHilf += 4;
						
						karteArray[j][i] = convertByteArrayToInt(hilfs);
					}
				}
				
				//Spieloptionen KartenArray setzten
				optionen.mapTileNum = karteArray;
				optionen.starteClientAnsicht++; //Empfangen so dass man die Ansicht starten kann
			}
			});
		
		//Empfange das Ereigniss AddCarView
				registerMethod(8, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						System.out.println("Client: hat Nachricht 8 erhalen, addCarView");
						
						byte[] daten = msg.data;
						int intHilf = 0;
						byte[]hilfs = new byte[4];
						
						int SpielerID;
						String AutoTyp ="";
						int worldX;
						int worldY;
						String direction = "";
						
						//Hole ID
						for(int k = 0;k<4; k++) {
							hilfs[k] = daten[intHilf + k];
						}intHilf += 4;
						SpielerID = convertByteArrayToInt(hilfs);
						
						//HoleAutoTyp
						for(int k = 0;k<4; k++) {
							hilfs[k] = daten[intHilf + k];
						}intHilf += 4;
						int intAutotyp = convertByteArrayToInt(hilfs);
						switch(intAutotyp) {
						case 1: //RotesAuto
							AutoTyp = "RotesAuto";
							break;
						case 2: //RotesAuto
							AutoTyp = "BlauesAuto";
							break;
						case 3: //RotesAuto
							AutoTyp = "GruenesAuto";
							break;
						case 4: //RotesAuto
							AutoTyp = "GelbesAuto";
							break;
						}
						//XKoordinate
						for(int k = 0;k<4; k++) {
							hilfs[k] = daten[intHilf + k];
						}intHilf += 4;
						worldX = convertByteArrayToInt(hilfs);
						
						//YKoordinate
						for(int k = 0;k<4; k++) {
							hilfs[k] = daten[intHilf + k];
						}intHilf += 4;
						worldY = convertByteArrayToInt(hilfs);
						
						//YKoordinate
						for(int k = 0;k<4; k++) {
							hilfs[k] = daten[intHilf + k];
						}intHilf += 4;
						int directionInt = convertByteArrayToInt(hilfs);
						switch (directionInt) {
						case 1:
							direction = "up";
							break;
						case 2:
							direction = "down";
							break;
						case 3:
							direction = "left";
							break;
						case 4:
							direction = "right";
							break;
						}
					
					while(app.spiellogik.spielAnsichten == null) {
						
					}
					
					double dworldX = worldX;
					double dworldY = worldY;
					
						
					event.notify("add_view_car",SpielerID, AutoTyp, dworldX, dworldY, direction);
						
					}
					});
				
				//empfange die KartenTiles
				registerMethod(9, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						System.out.println("Client: hat Nachricht 9 erhalen, KartenTiles");
						
						byte[] daten = msg.data;
						int length = msg.datalength;
						
						Map <Integer, BufferedImage>spielfeldTiles = new HashMap<Integer, BufferedImage>();
						try {
						for(int i = 0; i< length;) {
							byte[]hilfs = new byte[4];
							//Get TileID
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[i + k];
							}
							int tileID = convertByteArrayToInt(hilfs);
							i+= hilfs.length;
							//Get Tiledatalength
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[i + k];
							}
							int tileLaenge = convertByteArrayToInt(hilfs);
							i+= hilfs.length;
							//Get TileData
							hilfs = new byte[tileLaenge];
							for(int k = 0; k<tileLaenge;k++) {
								hilfs[k] = daten[i+k];
							}
							//Convert it to Buffered Image
							BufferedImage image = toBufferedImage(hilfs);
							i+= hilfs.length;
							
							spielfeldTiles.put(tileID, image);
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					optionen.spielfeldTiles = spielfeldTiles;	
					optionen.starteClientAnsicht++; //Empfangen so dass man die Ansicht starten kann	
					}
					});
				
				//Empfange die AutoTiles
				registerMethod(10, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						System.out.println("Client: hat Nachricht 10 erhalen, AutoTiles");
						
						byte[]daten = msg.data;
						int lenge = msg.datalength;
						
						Map<Integer,Map<Integer, BufferedImage>> autoTile = new HashMap<Integer,Map<Integer, BufferedImage>>();
						
						byte[] hilfs = new byte[4];
						
						for(int intHilf = 0; intHilf<lenge;) {
							//HoleAutoID
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 4;
							int autoID = convertByteArrayToInt(hilfs);
							
							//Hole Map mit AutoTiles
							Map <Integer, BufferedImage>autoTiles = new HashMap<Integer, BufferedImage>();
							try {
							for(int i = 0; i < 4; i++) { //Fuer 4 Bilder
								//Get AutoTileID
								hilfs = new byte[4];
								for(int k = 0;k<4; k++) {
									hilfs[k] = daten[intHilf + k];
								}
								int autotileID = convertByteArrayToInt(hilfs);
								intHilf+= hilfs.length;
								
								//Get Tiledatalength
								for(int k = 0;k<4; k++) {
									hilfs[k] = daten[intHilf + k];
								}
								int autotileLaenge = convertByteArrayToInt(hilfs);
								intHilf+= hilfs.length;
								
								//Get TileData
								hilfs = new byte[autotileLaenge];
								for(int k = 0; k<autotileLaenge;k++) {
									hilfs[k] = daten[intHilf+k];
								}
								//Convert it to Buffered Image
								BufferedImage image = toBufferedImage(hilfs);
								intHilf+= hilfs.length;
								
								autoTiles.put(autotileID, image);
								
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
							
							autoTile.put(autoID, autoTiles);
						}
						optionen.autoTiles = autoTile;
						optionen.starteClientAnsicht++; //Empfangen so dass man die Ansicht starten kann	
					}
					});
				
//				//starteAnsicht
//				registerMethod(11, new Executable() {
//					@Override
//					public void run(Datapackage msg, Socket socket) {
//						Log("Client: hat Nachricht 11 erhalen, starteAnsicht");
//						event.notify("starte_ansicht");
//					}
//				});
				
				//aendereStatus Start
				registerMethod(12, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						Log("Client: hat Nachricht 12 erhalen, aendereStatusStart");
						event.notify("aendere_status_start",Spielstadien.Status_Spiel_starten);
					}
				});
				
				//aendereCountdown
				registerMethod(13, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						Log("Client: hat Nachricht 13 erhalen, Countdown");
						byte[]daten = msg.data;
						String string;
						int nummer = convertByteArrayToInt(daten);
						if(nummer == 4) {
							string = "Start";
						}
						else {
							string = Integer.toString(nummer);
						}
						event.notify("aendere_status_countdown", string);
						
					}
				});
				
				//aendereStatus Spiel_Laeuft
				registerMethod(14, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						Log("Client: hat Nachricht 14 erhalen, aendereStatusstart");
						event.notify("aendere_status_start",Spielstadien.Status_Spiel_Laeuft);
					}
				});
				
				//aendereStatus Spiel_Beendet
				registerMethod(15, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						Log("Client: hat Nachricht 15 erhalen, aendereStatusbeendet");
						
						byte[] daten = msg.data;
						int laenge = msg.datalength;
						
						int objectlaenge = laenge/(4+4+8) + 1;
						
						Object [] o = new Object[objectlaenge*3];//todo
						int i = 0;
						for(int intHilf = 0; intHilf <laenge;) {
							
							byte[] hilfs = new byte[4];
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 4;
							int spielerID = convertByteArrayToInt(hilfs);
							
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 4;
							int rundenanzahl = convertByteArrayToInt(hilfs);
							
							hilfs = new byte[8];
							for(int k = 0;k<8; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 8;
							double zeit = toDouble(hilfs); 
							
							o[i] = spielerID;
							i++;
							o[i] = rundenanzahl;
							i++;
							o[i]= zeit;
							i++;
						}

						int sieger = optionen.werteErgebnisseAus(o, false);
						o[o.length-1] = sieger;
						
						event.notify("aendere_status_beendet",Spielstadien.Status_Spiel_Beendet, o);
					}
				});
				
				
				//getCheckpoints
				registerMethod(16, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						System.out.println("Client: hat Nachricht 16 erhalen, getCheckpoints");
						
						Map<Integer,List<TileKoordinate>> checkpointListe = new HashMap<Integer,List<TileKoordinate>>();
						int maxCheckpoint = 0;
						
						byte[] daten = msg.data;
						int laenge = msg.datalength;
						
						int intHilf = 0;
						int anzahlCheckpoints = laenge/(4*3); //Laenge durch 4Bytes und 3 Werte
						for(int i = 0 ; i < anzahlCheckpoints; i++) {
							
							byte[] hilfs = new byte[4];
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 4;
							int checkpointNummer = convertByteArrayToInt(hilfs);
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 4;
							int checkpointTileX = convertByteArrayToInt(hilfs);
							for(int k = 0;k<4; k++) {
								hilfs[k] = daten[intHilf + k];
							}intHilf += 4;
							int checkpointTileY = convertByteArrayToInt(hilfs);
							
							//Schaffe Liste und setzte checkpoints
							if(!checkpointListe.containsKey(checkpointNummer)) {
								List<TileKoordinate> checkList = new ArrayList<>();
								checkList.add(new TileKoordinate(checkpointTileX, checkpointTileY));
							checkpointListe.put(checkpointNummer, checkList);
							}
							else {
								//F�ge Element der Checkpointliste hinzu
								List<TileKoordinate> checkList = checkpointListe.get(checkpointNummer);
								checkList.add(new TileKoordinate(checkpointTileX, checkpointTileY));
								checkpointListe.put(checkpointNummer, checkList);
							}
							maxCheckpoint = checkpointNummer; //Setzte den maximalen Checkpoint
							
						}
						
						optionen.checkpointListe = checkpointListe;
						optionen.maxCheckpoint = maxCheckpoint;
						
						
					}
				});
				
				
				//getTileInformationen
				registerMethod(17, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						System.out.println("Client: hat Nachricht 17 erhalen, tileInformationen");
						
						Map<Integer,Object[]> checkpointListe = new HashMap<Integer,Object[]>();
						
						byte[] daten = msg.data;
						int laenge = msg.datalength;
						
						int anzahlTileInformationen = laenge/(4+8+1+1); //4Byte Nummber, 8 Byte Reibung, 1 Byte collision, 1 Byte ziel
						int intHilfs = 0;
						for(int i = 0; i < anzahlTileInformationen; i++ ) {
							//Nummer
							byte[] hilfs = new byte[4];
							for(int j = 0; j<4; j++) {
								hilfs[j] = daten[j+intHilfs];
							}intHilfs += 4;
							int tileNummber = convertByteArrayToInt(hilfs);
							//reibung
							hilfs = new byte[8];
							for(int j = 0; j<8; j++) {
								hilfs[j] = daten[j+intHilfs];
							}intHilfs += 8;
							double reibung = toDouble(hilfs);
							//collision
							byte collisionByte = daten[intHilfs];
							intHilfs +=1;
							boolean collision = (boolean)(collisionByte == 1 ? true : false);
							//ziel
							byte zielByte = daten[intHilfs];
							intHilfs +=1;
							boolean ziel = (boolean)(zielByte == 1 ? true : false);
							
							
							Object[] o = new Object[5];
							o[2]= reibung;
							o[3]= collision;
							o[4]= ziel;
							
							checkpointListe.put(tileNummber, o);
						}
						//Setzten der Checkpoint Client Liste
						optionen.tileInformationen = checkpointListe;
					}
				});
	}
	
	/**
	 * Methode die die den Start der ClientNetzwerkverbindung managed
	 */
	private void ClientStart() {
		stopped = false;
		ClientLogin();
		ClientStartListening();
		
	}

	/**
	 * Start des Listening Thread vom Server. Fuer einkommende Nachrichten werden in Datenpackete decodiert und ein eigener Thread erstellt fuer die Weiterbearbeitung
	 */
	 private void ClientStartListening() {

		 if (listeningThread != null && listeningThread.isAlive()) {
		      return;
		    } 
		
		 listeningThread = new Thread(new Runnable() {
		      @Override
		      public void run() {

		        // always repeat if not stopped
		        while (!stopped) {
		          try {
		            // repair connection if something went wrong with the connection
		            if (loginSocket != null && !loginSocket.isConnected()) {
		              while (!loginSocket.isConnected()) {
		                repairConnection();
		                if (loginSocket.isConnected()) {
		                  break;
		                }

		                Thread.sleep(5000);
		                repairConnection();
		              }
		            }

		            onConnectionGood();

		            // wait for incoming messages and read them
		            BufferedInputStream bis = //Einlesen von Datenstroemen
		                new BufferedInputStream(loginSocket.getInputStream());
		            
		            Datapackage message = new Datapackage(); //Umwandeln des Datenstroms in ein Datapackage
		            message.readByteMessage(bis);
		            
		            Log("Client: hat eine Nachricht empfangen Message iD : " + message.getId());
		            //System.out.println("Client: hat eine Nachricht empfangen Message iD : " + message.id);

		            // if the client has been stopped while this thread was listening to an arriving
		            // Datapackage, stop the proccess at this point
		            if (stopped) {
		              return;
		            }

		            if (message instanceof Datapackage) {
		              //final Datapackage msg = (Datapackage) raw;

		              // inspect all registered methods
		              for (final int current : idMethods.keySet()) {
		                // if the identifier of a method equals the identifier of the Datapackage...
		                if (current == message.getId()) {
		                  onLog("[Client] Message received. Executing method for '" + message.getId() + "'...");
		                 // System.out.println("[Client] Message received. Executing method for '" + message.id() + "'...");
		                  // execute the registered Executable on a new thread
		                  new Thread(new Runnable() {
		                    @Override
		                    public void run() {
		                      idMethods.get(current).run(message, loginSocket);
		                    }
		                  }).start();
		                  break;
		                }
		              }

		            }

		          } catch (SocketException e) {
		            onConnectionProblem();
		            onLog("[Client] Connection lost");
		            repairConnection();
		          } catch (IOException | InterruptedException ex) {
		            ex.printStackTrace();
		            onConnectionProblem();
		            onLog("[Client] Error: The connection to the server is currently interrupted!");
		            repairConnection();
		          }

		          // reset errorCount if no errors occured until this point
		          errorCount = 0;

		        } // while not stopped

		      } // run
		    });

		    // start the thread
		    listeningThread.start();
		  }
	 
	 /**
	  * Methode die Datenpackete an den Server sendet
	  * @param message	Datenpacket
	  * @param timeout	Timeout
	  */
	 	public void ClientSendMessage(Datapackage message, int timeout) {
	 		try {
	 		      // connect to the target client's socket
	 		      Socket tempSocket;
	 		      //oeffne eine neue Socket
	 		        tempSocket = new Socket();
	 		        tempSocket.connect(address, timeout);
	 		      

	 		      // Open output stream and write message
	 		      
	 		      BufferedOutputStream tempOOS = new BufferedOutputStream(tempSocket.getOutputStream());

	 		      //Transform the Datapackage in einen Byte Stream
	 		      
	 		      tempOOS.write(message.completeMessage);
	 		      tempOOS.flush();
	 		     //Log("Client: hat eine Nachricht versendet " + message.id);

	 		      // close all streams and the socket
	 		      tempOOS.close();
	 		      tempSocket.close();

	 		    } catch (EOFException ex) {
	 		      onLog(
	 		          "[Client] Error right after sending message: EOFException (did the server forget to send a reply?)");
	 		    } catch (IOException ex) {
	 		      onLog("[Client] Error while sending message");
	 		      ex.printStackTrace();
	 		    }


	 	}
	 
	 	/**
	 	 * Methode die einen Verbindungsabbruch erneuert
	 	 */
		private void repairConnection() {
			onLog("[Client] Repairing connection...");
		    if (loginSocket != null) {
		      try {
		        loginSocket.close();
		      } catch (IOException e) {
		        // This exception does not need to result in any further action or output
		      }
		      loginSocket = null;
		    }

		    ClientLogin();
		    ClientStartListening();
			
		}
	 
		/**
		 * Starte den Verbindungsaufbau zum Server
		 */
	private void ClientLogin() {
		
		 if(stopped) {
			 return;
		 }
		// 1. connect
		 try {
			 onLog("[Client] Connecting" );
		      if (loginSocket != null && loginSocket.isConnected()) {
		        throw new AlreadyConnectedException();
		      }
		        loginSocket = new Socket();
		        loginSocket.connect(this.address, this.timeout);
		        
		      onLog("[Client] Connected to " + loginSocket.getRemoteSocketAddress());

		      // 2. login
		      try {
		        onLog("[Client] Logging in...");
		        // open an outputstream
		        BufferedOutputStream out = new BufferedOutputStream(loginSocket.getOutputStream());
		        // create a magic login package
		        Datapackage loginPackage;
		      
		        
		        	loginPackage =  new Datapackage();//Ein Login Package senden und hier generieren//LoginDatapackage new Datapackage("_INTERNAL_LOGIN_", id, group);
		        	loginPackage.setDatenDatapackage(1, 4, convertIntToByteArray(id)); //Login Datapackage
		        
		        //loginPackage.sign(id, group);
		        // send the package to the server
		        out.write(loginPackage.completeMessage);
		        out.flush();
		        // note: this special method does not expect the server to send a reply
		        onLog("[Client] Logged in.");
		        onReconnect();
		      } catch (IOException ex) {
		        onLog("[Client] Login failed.");
		      }
			 
			 
		 }catch (ConnectException e) {
		      onLog("[Client] Connection failed: " + e.getMessage());
		      onConnectionProblem();
		 } catch (IOException e) {
		      e.printStackTrace();
		      onConnectionProblem();
		 }
		
	}
	
	/**
	 * DebugMethode ob eine gute Netzwerkverbinung besteht
	 */
	public void onConnectionGood() {
		//System.out.println("Es besteht eine gute Verbindung zum Server");
	  }
	/**
	 * DebugMethode das eine schlechte Netzwerkverbinung besteht
	 */
	private void onConnectionProblem() {
		System.out.println("Client "+  id + " hat Probleme beim einlogggen in den Server");
		
	}
	
	/**
	 * DebugMethode Client hat sich erfolgreich nach einem Verbindungsabruch wieder verbunden
	 */
	private void onReconnect() {
		System.out.println("Client" +  id + " hat sich eingeloggt");// 
		
	}


	/**
	 * Initialiserung der Klasse als Server, Manage den Start des Servers
	 */
	private void setServerKonstruktor() {
		event.subscribe("add_view_car", this);
		event.subscribe("update_koordinate", this);
		event.subscribe("aendere_status_start", this);
		event.subscribe("aendere_status_countdown", this);
		event.subscribe("aendere_status_laeuft", this);
		event.subscribe("aendere_status_beendet", this);
		
		this.clients = new ArrayList<RemoteClient>(); //neu Array List von Clients
		this.port = optionen.port;
		this.muted = false;
		
		ServerPreStart();
		
		ServerStart();
		
		startPingThread();
		
	}

	/**
	 * Oranisiere den Start des Servers
	 */
		private void ServerStart() {
			stopped = false;
		    server = null; //ServerSocket
		    
		    try {
				server = new ServerSocket(port); //Eine neue Server Socket erstellen
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		ServerStartListening();
	}

		/**
		 * Starte den Verbindungsthread fuer die Clients
		 */
	private void ServerStartListening() {
		 if (listeningThread == null && server != null) {
		      listeningThread = new Thread(new Runnable() { //Starte einen neuen Thread

		        @Override
		        public void run() {	//neuer Thread
		          while (!Thread.interrupted() && !stopped && server != null) { //Bedingungen das der Server am laufen bleibt

		            try {
		              // Wait for client to connect
		              //onLog("[Server] Waiting for connection");
		              final Socket tempSocket = server.accept(); // potential resource leak, tempSocket might not be closed! //neue Client Socket wird angelegt

		              // Read the client's message
		              BufferedInputStream ois = new BufferedInputStream(tempSocket.getInputStream()); //lie�t die Daten ein die vom Client kommen
		              Datapackage pack = new Datapackage();
		              pack.readByteMessage(ois);
		              
		              //wird in ein neues Objekt angelegt//Hier inputstream in ein Datenpacket umwandeln

		              if (pack instanceof Datapackage) {			
		                //final Datapackage msg = (Datapackage) raw; //Generieen eines Datenpackage
		                //onLog("[Server] Message received: " + pack); 

		                // inspect all registered methods
		                for (final int current : idMethods.keySet()) {	//Suchen einer passenden Methode fuer die ID des Datenpackage
		                  // if the current method equals the identifier of the Datapackage...
		                  if (pack.getId() == current) {			//Falls id von Datenpackage und Id von Bearbeitungsfunktion uebereinstimmt
		                    //onLog("[Server] Executing method for identifier '" + pack.id() + "'");
		                    // execute the Executable on a new thread
		                    new Thread(new Runnable() { //Starte einen neuen Thread und fuehre die Funktion aus
		                      @Override
		                      public void run() {
		                        // Run the method registered for the ID of this Datapackage
		                        idMethods.get(current).run(pack, tempSocket); //Ausfuehren der Funktion
		                        // and close the temporary socket if it is no longer needed
		                        if (!(pack.getId() == LOGIN_ID)) { //IF Bedingung falls es die Connect Verbindung war halten wir die verbindung offen, ansonsten wird der Thread geschlossen.		     
		                          try {
		                            tempSocket.close();
		                          } catch (IOException e) {
		                            e.printStackTrace();
		                          }
		                        }
		                      }
		                    }).start();
		                    break;
		                  }
		                }

		              }

		            } catch (SocketException e) {
		              onLog("Server stopped.");
		              onServerStopped();
		            } catch (IllegalBlockingModeException | IOException e) {
		              e.printStackTrace();
		            }

		          }
		        }

		      });

		      listeningThread.start();
		    }
			
		}
	
	/**
	 * Sende eine Antwort an den Client
	 * @param toSocket Socket des Client
	 * @param message	Datenpaket
	 */
	 public synchronized void sendReply(Socket toSocket, Datapackage message) {
		    sendMessage(new RemoteClient(-1, toSocket),  message);
		  }
	 
	 /**
	  * Sende eine Nachricht an einen bestimmten Client
	  * @param remoteClient Clientdateb
	  * @param message		Datenpacket
	  */
	 public synchronized void sendMessage(RemoteClient remoteClient, Datapackage message) {
		    try {
		      // send message
		      if (!remoteClient.getSocket().isConnected()) {
		        throw new ConnectException("Socket not connected.");
		      }
		      BufferedOutputStream out = new BufferedOutputStream(remoteClient.getSocket().getOutputStream());
		      
		      //Generiere eine Byte Nachricht aus dem Datenpacket
		      
		      out.write(message.completeMessage);
		      out.flush();
		      //System.out.println("Hat die Nachricht versendet: " + message.id);
		    } catch (IOException e) {
		      System.out.println("[Server] [Send Message] Error: " + e.getMessage());

		      // if an error occured: remove client from list
		      if (toBeDeleted != null) {
		        toBeDeleted.add(remoteClient);
		      } else {
		        clients.remove(remoteClient);
		        onClientRemoved(remoteClient);
		      }
		    }
		  }

	 
	 /** Sende eine Nachricht an alle verbundenen Clients
	  * @param message Die Nachricht
	   * @return Die Anzahl der Clients an die, die Nachricht versendet wurde
	   */
	  public synchronized int broadcastMessage(Datapackage message) {
	    toBeDeleted = new ArrayList<RemoteClient>();

	    // send message to all clients
	    int txCounter = 0;
	    for (RemoteClient current : clients) {
	      sendMessage(current, message);
	      txCounter++;
	    }

	    // remove all clients which produced errors while sending
	    txCounter -= toBeDeleted.size();
	    for (RemoteClient current : toBeDeleted) {
	      clients.remove(current);
	      onClientRemoved(current);
	    }

	    toBeDeleted = null;

	    return txCounter;
	  }
	 
	 
	 
	/**
	 * Definition aller Methoden, um Datenpackete auszulesen die vom Client gesendet werden
	 */
	private void ServerPreStart() {
		
		//LoginPacket
		registerMethod(LOGIN_ID, new Executable() {
			@Override
		      public void run(Datapackage msg, Socket socket) {
				Log("Server: hat Nachricht 1 erhalen, Login");
		    	  int id = convertByteArrayToInt(msg.data); //Message ID
		    	  
		    	  if(id == 0) {
		    		  //registiere einen neuen Client
		    		  //holt eine neue Id
		    		  int cid = app.spiellogik.GetNeueAkteurID(); //Holt sich aus der SPiellogik eine neue Akteur id
		    		  registerClient(cid,socket); //registriere einen neuen Client
		    		  //sendet neue Id zurueck
		    		  
		    		  Datapackage idpack = new Datapackage();
		    		  idpack.setDatenDatapackage(2, 4, convertIntToByteArray(cid)); //sende die neue ID zurueck //MessageID =2 , //Byte 4
		    		  Log("Server: es soll Datenpacket 2 gesendet werden");
		    		  //Senden der ID
		    		  
		    		  sendReply(socket, idpack);
		    		  Log("Server: hat Nachricht 2 gesendet, SpielerID");

					  try {
						  Thread.sleep(500);
					  } catch (InterruptedException e) {
						  // TODO Auto-generated catch block
						  e.printStackTrace();
					  }
		    		  
		    		  //Spielfeld 
		    		  ServerSendeSpielfeld(socket);
		    		  
		    		  //BilderKartenFliesen
		    		  ServerSendeKartenFliesen(socket);

		    		  //Tiles Auto
		    		  ServerSendeAutoTiles(socket);

		    		  //Sende Checkpoint
		    		  ServerSendeCheckpoint(socket);

		    		  //Sende TileKartenInformationen
		  			  ServerSendeTileKartenInformationen(socket);

		    		  //Uebertrage ListeAutos
		    		  ServerSendeListeAutos(socket);


		    		  try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		  //Test
		    		  ServerSendeAkteurListe(socket);

		    	  }
		    	  else if(id >= 0) {
		    		  //Neue Socket dem vorhanden Client zuweisen
		    		  for(RemoteClient rc : clients) {
		    			  if(id == rc.getId() && socket != rc.getSocket()) {
		    				  rc.setSocke(socket);
		    			  }
		    		  }
		    		  
		    	  }
		        onClientRegistered();
		      }
		    });
		
		//Method Client setAutoID
				registerMethod(4, new Executable() {
					@Override
					public void run(Datapackage msg, Socket socket) {
						Log("Server: hat Nachricht 4 erhalten,setzte AutoID fuer spieler"); 
						int spielerID;
						int autoID;
						
						byte [] daten =msg.data;// msg.completeMessage;
						byte []	hilfs = new byte[4];
						
						for(int i = 0; i<4; i++) {
							hilfs[i] = daten[i];
						}
						spielerID = convertByteArrayToInt(hilfs);
						for(int i = 0; i<4; i++) {
							hilfs[i] = daten[4+i];
						}
						autoID = convertByteArrayToInt(hilfs);
						
						event.notify("add_spielakteur", spielerID, autoID);
						
					}
			});
		
		
		
		//Method Tastatur-Input
		registerMethod(6, new Executable() {
			@Override
			public void run(Datapackage msg, Socket socket) {
				//Log("Server: hat Nachricht 6 erhalten, SpielerInput");
				int spielerID;
				boolean up;
				boolean down;
				boolean left;
				boolean right;
				
				byte [] daten = msg.data;
				
				byte[]hilfs = new byte[4];
				for(int i = 0; i<4; i++) {
					hilfs[i] = daten[i];
				}
				spielerID = convertByteArrayToInt(hilfs);
				up = (daten[4]== 1 ? true : false);
				down = (daten[5]== 1 ? true : false);;
				left = (daten[6]== 1 ? true : false);;
				right = (daten[7]== 1 ? true : false);;
				
				//StarteEvent
				event.notify("key_event", spielerID, up, down, left, right);
			}
	});
		
		
		
	}

	/**
	 * Methode die die TileKarten Infomationen an den Client sendet
	 * @param socket
	 */
	protected void ServerSendeTileKartenInformationen(Socket socket) {
		Datapackage pack = new Datapackage();
		int length = 0;
		int packID = 17;

		//Setze SpielerID für den Server
		optionen.spielerID = 1;
		Map<Integer, Object[]> tileInfomationen = optionen.tileInformationen;
		
		List<Byte> informationenListByte = new ArrayList<Byte>();
		
		if(tileInfomationen != null) {
			try {
			for(Map.Entry<Integer, Object[]> entry : tileInfomationen.entrySet()) {
				//TileNummer
				int tileNummer = entry.getKey();
				byte [] tileNummerByte = convertIntToByteArray(tileNummer);
				length += 4;
				
				Object[] tileInfo = entry.getValue();
				
				//Reibung
				double reibung = (double) tileInfo[2];
				byte[] reibungByte = doubleToByteArray(reibung);
				length += 8;
				//collision
				boolean collision = (boolean) tileInfo[3];
				byte collisionByte = (byte)(collision?1:0);
				byte[]collisionByteArray = {collisionByte};
				length += 1;
				//Ziel
				boolean ziel = (boolean) tileInfo[4];
				byte zielByte = (byte)(ziel?1:0); 
				byte[]zielByteArray = {zielByte};
				length += 1;
				
				informationenListByte.addAll(convertBytesToList(tileNummerByte));
				informationenListByte.addAll(convertBytesToList(reibungByte));
				informationenListByte.addAll(convertBytesToList(collisionByteArray));
				informationenListByte.addAll(convertBytesToList(zielByteArray));

				
			}
			
			Byte[] bytes = informationenListByte.toArray(new Byte[informationenListByte.size()]);
			byte[] combined = new byte[bytes.length];
			//Convertiere es in byte[]
				for(int i = 0; i< bytes.length; i++) {
					combined[i] = (byte) bytes[i];
				}

				pack.setDatenDatapackage(packID, length, combined);
				//Sende die Karte an alle
				Log("Server: hat Nachricht 17 gesendet, tileInformationen");
				sendReply(socket,pack);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			
		}
		
	}

	/**
	 * Methode die die Checkpoints an den Client sendet
	 * @param socket
	 */
	protected void ServerSendeCheckpoint(Socket socket) {
		Datapackage pack = new Datapackage();
		int length = 0;
		int packID = 16;
		
		Map<Integer, List<TileKoordinate>> checkpointMap = optionen.checkpointListe;
		
		List<Byte> checkpointListByte = new ArrayList<Byte>();
		
		
		if(checkpointMap!=null) {
			
			for(int checkpointNumber = 1; checkpointNumber <= optionen.maxCheckpoint; checkpointNumber++) {
				
				List<TileKoordinate> checkpointList = checkpointMap.get(checkpointNumber);
				
				for(int i = 0; i < checkpointList.size(); i++) {
					TileKoordinate cord = checkpointList.get(i);
					
					//CheckpointNumber
					byte[] checkpointNumberByte = convertIntToByteArray(checkpointNumber);
					length += 4;
					//Checkpoint X Koordinate
					byte[] checkpointXByte = convertIntToByteArray(cord.getTileX());
					length += 4;
					//Checkpoint Y Koordinate
					byte[] checkpointYByte = convertIntToByteArray(cord.getTileY());
					length += 4;
					
					checkpointListByte.addAll(convertBytesToList(checkpointNumberByte));
					checkpointListByte.addAll(convertBytesToList(checkpointXByte));
					checkpointListByte.addAll(convertBytesToList(checkpointYByte));
				}
				
			}
			
			Byte[] bytes = checkpointListByte.toArray(new Byte[checkpointListByte.size()]);
			byte[] combined = new byte[bytes.length];
			//Convertiere es in byte[]
				for(int i = 0; i< bytes.length; i++) {
					combined[i] = (byte) bytes[i];
				}

				pack.setDatenDatapackage(packID, length, combined);
				//Sende die Karte an alle
				Log("Server: hat Nachricht 16 gesendet, sendeCheckpoints");
				sendReply(socket,pack);
	
		}
		
	}


	/**
	 * Sende einem Client die Liste der verbundenen Akteure
	 * @param socket Clientsocket
	 */
	protected void ServerSendeAkteurListe(Socket socket) {	

		for (Map.Entry<Integer, Spielakteur> entry : app.spiellogik.akteure.entrySet()) {
			entry.getValue();
			//uebertrage vorhandene Spieler
			
			
			Datapackage pack = new Datapackage();
			int length = 0;
			int packID = 8;
			
			int spielerID1 = entry.getValue().GetId(); //(int) eventData[0];
			String auto = entry.getValue().resource;// (String) eventData[1];
			int worldX = (int)entry.getValue().position.x/optionen.tileGroesse;//(int) ((double) eventData[2]);
			int worldY = (int) entry.getValue().position.y/optionen.tileGroesse;//((double)eventData[4]);
			String direction = entry.getValue().direction;// (String) eventData[4];
			
			byte[] spielerIDBytes = convertIntToByteArray(spielerID1);
			length += 4;
			
			byte[] autoBytes = null;
			if(auto == "RotesAuto" || auto.equals("RotesAuto")) { //Bekommt ID 1 //ueberpruefen ob das noch noetig ist 
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			else if(auto == "BlauesAuto" || auto.equals("BlauesAuto")) { //Bekommt ID 2
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			else if(auto == "GruenesAuto"|| auto.equals("GruenesAuto")) { //Bekommt ID 3
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			else if(auto == "GelbesAuto"|| auto.equals("GelbesAuto")) { //Bekommt ID 4
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			
			byte[] worldXBytes = convertIntToByteArray(worldX);
			length += 4;
			
			byte[] worldYBytes = convertIntToByteArray(worldY);
			length += 4;
			
			byte directionBytes[] = null;

			if(direction == "up") {
				int idir = 1;
				directionBytes =  convertIntToByteArray(idir);
			}
			if(direction == "down") {
				int idir = 2;
				directionBytes = convertIntToByteArray(idir);
			}
			if(direction == "left") {
				int idir = 3;
				directionBytes = convertIntToByteArray(idir);
			}
			if(direction == "right") {
				int idir = 4;
				directionBytes = convertIntToByteArray(idir);
			}
			length += directionBytes.length;
			
			byte[] allByteArray = new byte[spielerIDBytes.length + autoBytes.length + worldXBytes.length + worldYBytes.length + directionBytes.length];

			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			buff.put(spielerIDBytes);
			buff.put(autoBytes);
			buff.put(worldXBytes);
			buff.put(worldYBytes);
			buff.put(directionBytes);

			byte[] combined = buff.array();
			
//			for(int i=0; i< combined.length ; i++) {
//		         System.out.print(combined[i] +" ");
//		      }

			pack.setDatenDatapackage(packID, length, combined);
			//System.out.println("Server: Message 8 wurde versendet. add view car");
			sendReply(socket, pack);
			System.out.println("Server: Message 8 wurde versendet. add view car" + auto);
		}
		
	}
	/**
	 * Sende einem Client die Liste aller verfuegbaren Autos
	 * @param socket Clientsocket
	 */
	protected void ServerSendeListeAutos(Socket socket) {
			
		  Datapackage pack = new Datapackage();
		  int length = 0;
		  int packID = 3;
		  
		byte[] allByteArray = new byte[optionen.autoListe.size()*4 *4+4]; //4Bytes und 4 interger Werte die uebertragen werden

			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
				  
			  int anzahlAutos = optionen.autoListe.size();
			  byte[] anzahlAutosBytes = convertIntToByteArray(anzahlAutos);
			  length += 4;
			  buff.put(anzahlAutosBytes);
		  
		  for (Map.Entry<String, Object[]> entry : optionen.autoListe.entrySet()) {
			  
			 Object [] data= entry.getValue();
			 byte [] autoIDBytes = convertIntToByteArray((int)data[0]);
			 length += 4;
			 byte [] masseBytes = convertIntToByteArray((int)data[3]);
			 length += 4;
			 byte [] maxFahrgeschwindigkeitBytes = convertIntToByteArray((int)data[4]);
			 length += 4;
			 byte [] fahrkraftsteigerungBytes = convertIntToByteArray((int)data[5]);
			 length += 4;
			  
			buff.put(autoIDBytes);
			buff.put(masseBytes);
			buff.put(maxFahrgeschwindigkeitBytes);
			buff.put(fahrkraftsteigerungBytes);
		    }
		  byte[] combined = buff.array();
		  pack.setDatenDatapackage(packID, length, combined);
		  Log("Server: es soll Datenpacket 3 gesendet werden");
		  
		  sendReply(socket, pack);
		  Log("Server: hat Nachricht 3 gesendet, listeAutos");
		
	}
	
	/**
	 * Senden der Bilder die die Autos darstellen
	 * @param socket Clientsocket
	 */
	protected void ServerSendeAutoTiles(Socket socket) {
		Map<Integer, Map<Integer, BufferedImage>> mapAutoTiles = optionen.autoTiles;
		int packID = 10;
		Datapackage pack = new Datapackage();
		int length = 0;
		
		//neue Liste
		List<Byte> list = new ArrayList<Byte>();
		
		try {
		for (Map.Entry<Integer, Map<Integer, BufferedImage>> entry : mapAutoTiles.entrySet()) { //Alle Autotypen einlesen
		int autoID = entry.getKey();
		byte[] autoIDbyte = convertIntToByteArray(autoID);
		length += 4;
		list.addAll(convertBytesToList(autoIDbyte));
		
		Map<Integer, BufferedImage> map = entry.getValue();
			for(Map.Entry<Integer, BufferedImage> entry2 : map.entrySet()) {					//Bilder der Autotypen einlsen 
				
				int tileID = entry2.getKey();
				byte[] tileIDbyte = convertIntToByteArray(tileID);
				
				//fuege IDbytes hinzu
				list.addAll(convertBytesToList(tileIDbyte));			
				length += tileIDbyte.length;
				
				BufferedImage tileImage = entry2.getValue();
				byte[] tileImageBytes = toByteArray(tileImage, "png");
				length += tileImageBytes.length;
				
				int imagelength = tileImageBytes.length;
				byte[]imagelengthbyte = convertIntToByteArray(imagelength);
				length += 4;
				//Laenge der Bilddatei anhaengen
				list.addAll(convertBytesToList(imagelengthbyte));
				//Bytes der Bilddatei
				list.addAll(convertBytesToList(tileImageBytes));	
	
			}
			
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Byte[] bytes = list.toArray(new Byte[list.size()]);
		byte[] combined = new byte[bytes.length];
		//Convertiere es in byte[]
			for(int i = 0; i< bytes.length; i++) {
				combined[i] = (byte) bytes[i];
			}

			pack.setDatenDatapackage(packID, length, combined);
			//Sende die Karte an alle
			Log("Server: hat Nachricht 10 gesendet, sendeAutoTiles");
			sendReply(socket,pack);
			//broadcastMessage(pack);
		
	}
	/**
	 * Sende Client die Bilder der Spielkarte
	 * @param socket
	 */
	protected void ServerSendeKartenFliesen(Socket socket) {
		
		int packID = 9;
		int length = 0;
		Datapackage pack = new Datapackage();
		
		Map<Integer, BufferedImage> Tiles = optionen.spielfeldTiles;
		
		try {
			//neue Liste
			List<Byte> list = new ArrayList<Byte>();
			
			//Speicher sichern fuer alle Bilder
			//Alle Bilder auslesen und In ein ByteArray konvertieren
		for (Map.Entry<Integer, BufferedImage> entry : Tiles.entrySet()) {
			int tileID = entry.getKey();
			byte[] tileIDbyte = convertIntToByteArray(tileID);
			
			//fuege IDbytes hinzu
			list.addAll(convertBytesToList(tileIDbyte));			
			length += tileIDbyte.length;
			
			BufferedImage tileImage = entry.getValue();
			byte[] tileImageBytes = toByteArray(tileImage, "png");
			length += tileImageBytes.length;
			
			int imagelength = tileImageBytes.length;
			byte[]imagelengthbyte = convertIntToByteArray(imagelength);
			length += 4;
			//Laenge der Bilddatei anhaengen
			list.addAll(convertBytesToList(imagelengthbyte));
			//Bytes der Bilddatei
			list.addAll(convertBytesToList(tileImageBytes));
	    }
		
		Byte[] bytes = list.toArray(new Byte[list.size()]);
		byte[] combined = new byte[bytes.length];
		//Convertiere es in byte[]
		for(int i = 0; i< bytes.length; i++) {
			combined[i] = (byte) bytes[i];
		}
		
		pack.setDatenDatapackage(packID, length, combined);
		//Sende die Karte an alle
		Log("Server: hat Nachricht 9 gesendet, sendeKartenTiles");
		sendReply(socket, pack);
		//broadcastMessage(pack);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Server sendet dem Client das Spielfeld
	 * @param socket
	 */
	protected void ServerSendeSpielfeld(Socket socket) {
		  Datapackage pack = new Datapackage();
			int length = 0;
			KartenManager manager = KartenManager.getInstance();
			int packID = 7;
			
			byte [] kartenbyte = new byte[optionen.maxBildschirmZeilen*4 * optionen.maxBildschirmSpalten];
			
			ByteBuffer buff = ByteBuffer.wrap(kartenbyte);
			
			//Zeilenweise in Convertieren
			for(int i = 0; i<optionen.maxBildschirmZeilen; i++) {
				for(int j = 0; j< optionen.maxBildschirmSpalten; j++) {
					  buff.put(convertIntToByteArray(manager.mapTileNum[j][i]));
					  length += 4;
				}
			}
			
			byte[] combined = buff.array();
			
			pack.setDatenDatapackage(packID, length, combined);
			//sende Karte an alle
			broadcastMessage(pack);
			//sendReply(socket, pack);
			Log("Server: hat Nachricht 7 gesendet, sendeKarte");
		
	}
	
	/**
	 * startet einen Thread der in regelmae�igen Abstaenden die Neztwerkverbinundung aller Clients prueft
	 */
	private void startPingThread() {
		 new Thread(new Runnable() {
		      @Override
		      public void run() {

		        while (server != null) {
		          try {
		            Thread.sleep(pingInterval);
		          } catch (InterruptedException e) {
		            // This exception does not need to result in any further action or output
		          }
		          //broadcastMessage(new Datapackage("_INTERNAL_PING_", "OK")); Working on Datapackages
		        }

		      }
		    }).start();
		
	}
	
	
	/**
	 * Registierung eines neuen Clients, hinzufuegen der Client Liste
	 * @param id Client-ID
	 * @param newClientsocket Client Socket
	 */
	private void registerClient(int id, Socket newClientsocket) {
		clients.add(new RemoteClient(id, newClientsocket));
		Log("Server: hat hat einen neuen Client registriert");
		
	}
	
	/**
	 * registiere eine neue Datenpaket-Methode
	 * @param identifier Datenpaket-ID
	 * @param executalbe Executable
	 */
	public void registerMethod(int identifier, Executable executalbe) {
		idMethods.put(identifier, executalbe); //Fuegt neue Methode dem Netzwerkprotokoll hinzu
	}
	
	/**
	 * Debug Methode, gibt an ob ein Server gestoppt wurde
	 */
	private void onServerStopped() {
		System.out.println("Der Server wurde Beendet");// TODO Auto-generated method stub
		
	}
	/**
	 * Debug Methode die angibt ob der Client sich eingeloggt hat
	 */
	private void onClientRegistered() {
		System.out.println("Ein neuer Client hat sich eingelogt");
		
	}
	/**
	 * DebugMethode Client wurde aus der Liste entfernt
	 * @param current
	 */
	private void onClientRemoved(RemoteClient current) {
		System.out.println("Client " + current.getId()+" wurde entfernt");
		
	}

	/**
	 * Gibt Nachrichten aus, nur wenn Paramter muted = false ist
	 * @param string Nachricht
	 */
	private void onLog(String string) {
		if(!muted) {
			System.out.println(string);
		}
		
	}

	@Override
	/**Empfangen der Ereignisdaten
	 * @param eventType EreignisTyp
	 * @param current	EreingisDaten
	 */
	public void updateEvent(String eventType, Object... eventData) {
		
		//Auto der Ansicht hinzufuegen
		if(eventType == "add_view_car") {
			Datapackage pack = new Datapackage();
			int length = 0;
			int packID = 8;
			
			int spielerID1 = (int) eventData[0];
			String auto = (String) eventData[1];
			int worldX = (int) ((double) eventData[2]);
			int worldY = (int) ((double)eventData[3]);
			String direction = (String) eventData[4];
			
			
			byte[] spielerIDBytes = convertIntToByteArray(spielerID1);
			length += 4;
			
			byte[] autoBytes = null;
			if(auto == "RotesAuto" || auto.equals("RotesAuto")) { //Bekommt ID 1 //Ueberpruefen ob das noch noetig ist 
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			else if(auto == "BlauesAuto" || auto.equals("BlauesAuto")) { //Bekommt ID 2
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			else if(auto == "GruenesAuto" || auto.equals("GruenesAuto")) { //Bekommt ID 3
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			else if(auto == "GelbesAuto" || auto.equals("GelbesAuto")) { //Bekommt ID 4
				Object[] o = optionen.autoListe.get(auto);
				autoBytes = convertIntToByteArray((int)o[0]);
				length += 4;
			}
			
			byte[] worldXBytes = convertIntToByteArray(worldX);
			length += 4;
			
			byte[] worldYBytes = convertIntToByteArray(worldY);
			length += 4;
			
			byte directionBytes[] = null;

			if(direction == "up") {
				int idir = 1;
				directionBytes =  convertIntToByteArray(idir);
			}
			if(direction == "down") {
				int idir = 2;
				directionBytes = convertIntToByteArray(idir);
			}
			if(direction == "left") {
				int idir = 3;
				directionBytes = convertIntToByteArray(idir);
			}
			if(direction == "right") {
				int idir = 4;
				directionBytes = convertIntToByteArray(idir);
			}
			length += directionBytes.length;
			
			byte[] allByteArray = new byte[spielerIDBytes.length + autoBytes.length + worldXBytes.length + worldYBytes.length + directionBytes.length];
			
			//Kombiniere alle Arrays zu einem
			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			buff.put(spielerIDBytes);
			buff.put(autoBytes);
			buff.put(worldXBytes);
			buff.put(worldYBytes);
			buff.put(directionBytes);

			byte[] combined = buff.array();
			
			pack.setDatenDatapackage(packID, length, combined);
			//Senden der Koordinaten an alle
			Log("Server: hat Nachricht 8 gesendet, Auto der Ansichten hinzufuegen");
			broadcastMessage(pack);	
		}
		
		//Sende Tastertur Eingaben
		if(eventType == "key_event") {
			int packID = 6;
			int spielerID = (int) eventData[0];
			boolean up = (boolean) eventData[1];
			boolean down = (boolean) eventData[2];
			boolean left = (boolean) eventData[3];
			boolean right = (boolean) eventData[4];

			Datapackage pack = new Datapackage();
			int length = 0;
			
			byte spielerIDBytes[] = convertIntToByteArray(spielerID);
			length += spielerIDBytes.length;
			
			byte upbyte = (byte)(up?1:0);
			byte downbyte = (byte)(down?1:0);
			byte leftbyte = (byte)(left?1:0);
			byte rightbyte = (byte)(right?1:0);
			length += 4;
			
			byte[] allByteArray = new byte[length];
			
			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			buff.put(spielerIDBytes);
			buff.put(upbyte);
			buff.put(downbyte);
			buff.put(leftbyte);
			buff.put(rightbyte);

			byte[] combined = buff.array();
			
			pack.setDatenDatapackage(packID, length, combined);
			//Log("Client: hat Nachricht 6 gesendet, key-event");
			ClientSendMessage(pack, 10000);
			
		}
		
//		if(eventType == "starte_ansicht") {
//			
//			int packID = 11;
//			Datapackage pack = new Datapackage();
//			int length = 0;
//			byte[]b = {};
//			
//			pack.setDatenDatapackage(packID, length, b);
//			//Senden der Koordinaten an alle
//			Log("Server: hat Nachricht 11 gesendet, starteAnsichte");
//			broadcastMessage(pack);
//		}
		
		//Sende Statusaenderung Start
		if(eventType == "aendere_status_start") {//fehtl noch auffangFunktion
			
			int packID = 12;
			Datapackage pack = new Datapackage();
			int length = 0;
			byte[]b = {};
			
			pack.setDatenDatapackage(packID, length, b);
			//Senden der Koordinaten an alle
			Log("Server: hat Nachricht 12 gesendet, aendere_status_start");
			broadcastMessage(pack);
		}
		
		//Sende Statusaenderung Countdown
		if(eventType == "aendere_status_countdown") {
			//Log("Server: will Nachricht 13 gesendet, aendere_countdown");
			int packID = 13;
			Datapackage pack = new Datapackage();
			int length = 0;
			byte[]b = {};
			
			int countdown;
			if((String)eventData[0] == "Start") {
				countdown = 4;
			}
			else {
				countdown = Integer.parseInt((String)eventData[0]);
			}
			
			b = convertIntToByteArray(countdown);
			length = b.length;
			
			pack.setDatenDatapackage(packID, length, b);
			//Senden der Koordinaten an alle
			//Log("Server: hat Nachricht 13 gesendet, aendere_countdown");
			broadcastMessage(pack);
		}
		
		//Sende Statusaenderung Status Laeuft
		if(eventType == "aendere_status_laeuft") {//fehtl noch auffangFunktion
			
			int packID = 14;
			Datapackage pack = new Datapackage();
			int length = 0;
			byte[]b = {};
			
			pack.setDatenDatapackage(packID, length, b);
			//Senden der Koordinaten an alle
			Log("Server: hat Nachricht 14 gesendet, aendereStatuslaeuft");
			broadcastMessage(pack);
		}
		
		//Sende Statusaenderung beendet
		if(eventType == "aendere_status_beendet") {//fehtl noch auffangFunktion
			
			int packID = 15;
			
			Object[] o = (Object[]) eventData[1];
			
			Datapackage pack = new Datapackage();
			int length = 0;
			
			byte[] allByteArray = new byte[o.length/3 *(4+4+8)];//ByteAnzahl von spielerID, rundenAnzahl, und zeit
			
			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			
			try {
			for(int i = 0; i < o.length/3; i++) {
				
				int spielerId = (int) o[3*i];
				byte[] spielIdByte = convertIntToByteArray(spielerId);
				buff.put(spielIdByte);
				length += spielIdByte.length;
				
				int rundenAnzahl = (int) o[3*i+1];
				byte[] rundenAnzahlByte = convertIntToByteArray(rundenAnzahl);
				buff.put(rundenAnzahlByte);
				length += rundenAnzahlByte.length;
				
				double zeit = (double) o[3*i+2];
				byte[] zeitByte = doubleToByteArray(zeit);
				buff.put(zeitByte);
				length += zeitByte.length;
				
            }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			byte[] combined = buff.array();			
			
			pack.setDatenDatapackage(packID, length, combined);
			//Senden der Koordinaten an alle
			Log("Server: hat Nachricht 15 gesendet, aendere_status_beendet");
			broadcastMessage(pack);
		}
		
		//Sende Neue Koordinaten
		if(eventType == "update_koordinate") { //Getestet
			int packID = 5;
			Datapackage pack = new Datapackage();
			int length = 0;
			//eventData
			int spielerID = (int) eventData[0];
			String direction = (String) eventData[1];
			int spielerX = (int) eventData[2];
			int spielerY = (int) eventData[3];
			
			
			//Spieldaten
			byte spielerIDBytes[] = convertIntToByteArray(spielerID);
			length += spielerIDBytes.length;
			
			byte directionBytes[] = null;

			if(direction == "up") {
				int idir = 1;
				directionBytes =  convertIntToByteArray(idir);
			}
			if(direction == "down") {
				int idir = 2;
				directionBytes = convertIntToByteArray(idir);
			}
			if(direction == "left") {
				int idir = 3;
				directionBytes = convertIntToByteArray(idir);
			}
			if(direction == "right") {
				int idir = 4;
				directionBytes = convertIntToByteArray(idir);
			}
			length += directionBytes.length;
			
			
			byte XBytes[] = convertIntToByteArray(spielerX) ;
			length += XBytes.length;
			byte YBytes[] = convertIntToByteArray(spielerY) ;
			length += YBytes.length;
			
			
			byte[] allByteArray = new byte[spielerIDBytes.length + directionBytes.length + XBytes.length + YBytes.length];

			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			buff.put(spielerIDBytes);
			buff.put(directionBytes);
			buff.put(XBytes);
			buff.put(YBytes);

			byte[] combined = buff.array();
			
			for(int i=0; i< combined.length ; i++) {
		         //System.out.print(combined[i] +" ");
		      }
			
			
			pack.setDatenDatapackage(packID, length, combined);
			//Senden der Koordinaten an alle
			//Log("Server: hat Nachricht 5 gesendet, updateKoordinate");
			broadcastMessage(pack);
		}
		
		
	}
	
	//HilfeFunktionen
	/**
	 * Hilfunktion die Int to ByteArray konvertiert
	 * @param value Integerwert
	 * @return Byte[]
	 */
	public static byte[] convertIntToByteArray(int value) {
		 return new byte[] {
		            (byte)(value >> 24),
		            (byte)(value >> 16),
		            (byte)(value >> 8),
		            (byte)value};
   }
	/**
	 * Hilffunktion, die ByteArray zu Integer Konvertiert
	 * @param bytes Byte[]
	 * @return Integerwert
	 */
	 public static int convertByteArrayToInt(byte[] bytes) {
	        return ((bytes[0] & 0xFF) << 24) |
	                ((bytes[1] & 0xFF) << 16) |
	                ((bytes[2] & 0xFF) << 8) |
	                ((bytes[3] & 0xFF) << 0);
	    }
	 
	/**
	 * Hilfsfunktion die ein BufferedImage in ein Byte[] konveriert
	 * @param bi BufferedImage
	 * @param format Bildformat
	 * @return Byte[]
	 * @throws IOException
	 */
	    public static byte[] toByteArray(BufferedImage bi, String format)
	        throws IOException {

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(bi, format, baos);
	        byte[] bytes = baos.toByteArray();
	        return bytes;

	    }

/**
 * Hilfsfunktion, die ein Byte[] in ein BufferedImage konvertiert
 * @param bytes BildBytes[]
 * @return BufferedImage
 * @throws IOException
 */
	    public static BufferedImage toBufferedImage(byte[] bytes)
	        throws IOException {

	        InputStream is = new ByteArrayInputStream(bytes);
	        BufferedImage bi = ImageIO.read(is);
	        return bi;

	    }
	    
	  /**
	   * Hilfsfunktion die Bytes zu einer List konvertieren
	   * @param bytes
	   * @return list
	   */
	    private static List<Byte> convertBytesToList(byte[] bytes) {
	        final List<Byte> list = new ArrayList<>();
	        for (byte b : bytes) {
	            list.add(b);
	        }
	        return list;
	    }
	    
	    /**
	     * Hilfsfunktion die Double zu einer ByteArray konvertiern
	     * @param i Double-Wert
	     * @return byte[]
	     * @throws IOException
	     */
	    private byte[] doubleToByteArray ( final double i ) throws IOException {
	    	 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	 DataOutputStream dos = new DataOutputStream(bos);
	    	 dos.writeDouble(i);
	    	 dos.flush();
	    	 return bos.toByteArray();
	    	}
	    
	    /**
	     * Hilfsfunktion die ByteArray zu Double konvertiert
	     * @param bytes ByteArray
	     * @return Double-Wert
	     */
	    public static double toDouble(byte[] bytes) {
	        return ByteBuffer.wrap(bytes).getDouble();
	    }
	    
		private void Log(String string) {
			if(!muted) {
				System.out.println(string);
			}
			
		}
	
}