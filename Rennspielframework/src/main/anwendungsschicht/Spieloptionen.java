package anwendungsschicht;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kiansichtsschicht.AKI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import spiellogikschicht.Spielstadien;

import javax.imageio.ImageIO;


public class Spieloptionen { //Spieloptionen, die aus einer XML Datei geholt werden sollten

    private static Spieloptionen INSTANCE; //Singelton

    public Spieloptionen() {
    }

    //Spielfeldeinstellungen
    /**
     * Groe�e der SpielfeldTiles
     */
    public final int tileOrginalGroessen = 16; //16x16 Tile
    /**
     * Vergroe�erung der Tiles
     */
    public final int vergroesserung = 3;
    /**
     * Tile Groe�e ist fuer die Bildschirm- und Posisionsberechnung wichtig
     */
    public final int tileGroesse = tileOrginalGroessen * vergroesserung;
    /**
     * Festlegen der Bildschirmgroe�e auf 30 Spalten
     */
    public int maxBildschirmSpalten = 30;
    /**
     * Festlegen der Bildschirmgroe�e auf 20 Zeilen
     */
    public int maxBildschirmZeilen = 20;
    /**
     * Berechnete Bildschirmhoehe
     */
    public  int bildschirmHoehe = tileGroesse * maxBildschirmZeilen;
    /**
     * Berechnete Bildschirmbreite
     */
    public  int bildschirmBreite = tileGroesse * maxBildschirmSpalten;

    /**
     * Zeigt an ob das Grafikframework mit OpenGl inizialiert werden soll
     */
    public boolean isOpenGL = true;

    /**
     * Gibt an ob der Server Headless gestartet werden soll
     */
    public boolean isHeadless = false;
    //Netzwerkeinstellungen
    /**
     * Netzwerkport
     */
    public int port = 8000;
    /**
     * Netzwerkhostname
     */
    String hostname = "localhost";
    /**
     * Timeout fuer die Netzwerknachrichten
     */
    public int timeout = 10000;


    //Spieler
    /**
     * Ture wenn die Anwendung als Client ausgefuehrt wird
     */
    public boolean client;
    /**
     * Flag die Hochgezaehlt wird bis alle Nachrichten empfangen wurden, die noetig sind fuer das starten der menschlichen Ansicht
     */
    public int starteClientAnsicht = 0; //erst wenn alle Daten uebertragen wurden starte die ansicht
    /**
     * Anzahl der Spieler die auf dem Spiel spielen koennen
     */
    public int maxAnzahlSpieler = 2;
    public int AnzahlRomoteSpieler = 0;
    public int AnzahlKISpieler = 0;
    /**
     * Starten eines menschlichen Spielers auf dem Server
     */
    public boolean SpielerAufServer = false;
    /**
     * Spieltitel angezeigt auf dem Ausgabeframework
     */
    public final String titel = "2D Rennspiel Andre Zimmer";
    /**
     * Angabe der Spiellaenge in Sekunden
     */
    public int spiellaenge = 60; //In Sekunden
    //public final String world = "/Map/worldV2_00.txt"; //String von der Map

    /**
     * HoechsterCheckpointWert, wird benoetigt zur Kontrolle der richtigen Fahrweise
     */
    public int maxCheckpoint = 3; //Wert von der setCheckpointfunktion

    //Files
    /**
     * Pfad zur Map-Datei (Txt-Datei)
     */
    public String spielfeld = "/Map/worldV2_00.txt";
    /**
     * Pfad zu den Checkpoints-Datei (Txt-Datei)
     */
    /*public String checkpoints = ""
            + "Checkpoint_Map/worldV2_00_checkpoints.txt";
    *//**
     * Pfad zur startpositions-Datei (Txt-Datei)
     *//*
    public String startpositionen = "/Startpositionen/worldV2_00_startpositionen.txt";
    //public String logicFabrik = "/FactoryXML/AutoLogicFactory.xml";
    //public String logicFabrik = "C:/Users/Andr�/Desktop/Bachelorarbeit/ba-rennspielframework-andrezimmer-202122/Source/Bachelorarbeit - AZ - Rennspiel/Res/FactoryXML/AutoLogicFactory.xml";*/
    /**
     * Pfad zur Auto-Daten-Datei (JSON-Datei)
     */
    public String autoDatenDatei = "/Res/AutoConfig/AutoDaten.json";
    // Physikeinstellungen
    /**
     * Wert mit der die Staerke des Appralles regelt werden kann
     */
    public double abprallen = 2; //groe�ere Zahl weniger
    //public int fahrKraftsteigerung = 5000;

    /**
     * Status mit dem das Spiel Startet
     */
    public Spielstadien status = Spielstadien.Status_Initialisieren;

    //Spielfeld
    /**
     * HashMap in der alle autoTypen enthalten sind
     * Schluessel Autotyp (String) z.B. ("RotesAuto"),  Object[AutoID, AutoDatenkomponente(String) AutoPhysikkomponente(String), masse (int), maxGeschwindigkeit (int), fahrKraftsteigerung (int)
     */
    public Map<String, Object[]> autoListe = null; // String AutoTyp("RotesAuto"),
    /**
     * HashMap in der alle Checkpoints gespeichert werden
     */
    public Map<Integer, List<TileKoordinate>> checkpointListe = null;
    /**
     * Auflistung aller Spielfeldbilder Schluessel Bildnummer, Wert BufferedImage
     */
    public Map<Integer, BufferedImage> spielfeldTiles = null; //tiel Id //Buffered Tile Image
    /**
     * Auflistung aller AutoBilder, Schluessel Autonummer (rot = 1, blau = 2, gruen = 3, gelb = 4), zweiter Schluessel Ausrichtung der Auto (1 = open, 2 = unten 3 = links, 4, rechts), Wert BufferedImage
     */
    public Map<Integer, Map<Integer, BufferedImage>> autoTiles = null;
    /**
     * Map die Startpositionen enthalt ObjectArray [0] = x-Koordinate, [1] = y-Koordinate, [2] =  Ausrichtung (1 = open, 2 = unten 3 = links, 4, rechts)
     */
    public Map<Integer, Object[]> startpositionenArray = null; //startposition, XKoordinate(in Tiles), YKoordinate(in Tiles), direction
    /**
     * Integer Array[][] das die Positionierung der Bilder beinhaltet
     */
    public int mapTileNum[][]; //Spielfeld in einem Array //Initialisert nur vom Server
    /**
     * Karten Tile Informationen, es werden hier alle Informationen über die Tiles gespeichtert, Schluessel = TileNummer, Wert Object Array mit Werte [0] name(String), [1] Bildpfad (String), [2] Reibung (double), [3]collision (boolean), [4] ziel (boolean)
     * Bei einer ClientAnwendung werden Tilename und Tilepfad nicht übertragen und sind hier nicht zu finden
     */
    public Map<Integer, Object[]> tileInformationen = null;

    /**
     * Für den menschlichen Spielclient müssen die eingegangen add_view_car_Events abgefangen werden.
     */
    public Map<Integer, Object[]> addClientsInformation = null;

    /**
     * ID des Spielers, die vom Server gesetzt wird
     */
    public int spielerID = 0;

    public int spielmodus = 2;

    private int sieger = 0;


    /**
     * Auslesen der Kompletten Karten-Daten ein
     */
    public void LadeKarteInformation(){
        try{
            //LadeKartenInformation
            InputStream is;
            BufferedReader br;
            try {
                is = getClass().getResourceAsStream(spielfeld);
                br = new BufferedReader(new InputStreamReader(is));
            }
            //Datei ist nicht im Jar-Paket enthalten -> lese von Disk
            catch (Exception e) {
                is = new FileInputStream(spielfeld);
                br = new BufferedReader(new InputStreamReader(is));
            }
            String line = " ";
            line = br.readLine();
            System.out.println(line);

            //Einlesen der Spaltenanzahl und Zeilenanzahl
            line = br.readLine();
            String numb[] = line.split(" ");
            Object obj[] = new Object[numb.length];
            for (int i = 0; i < 2; i++) {
                obj[i] = Integer.parseInt(numb[i]);
            }
            maxBildschirmSpalten = (int) obj[0];
            maxBildschirmZeilen = (int) obj[1];
            bildschirmHoehe = maxBildschirmZeilen * tileGroesse;
            bildschirmBreite = maxBildschirmSpalten * tileGroesse;

            mapTileNum = new int[maxBildschirmSpalten][maxBildschirmZeilen];


                int col = 0;
                int row = 0;

                while (col < maxBildschirmSpalten && row < maxBildschirmZeilen) {

                    line = br.readLine();
                    while (col < maxBildschirmSpalten) {
                        String numbers[] = line.split(" ");
                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[col][row] = num;
                        col++;
                    }
                    if (col == maxBildschirmSpalten) {
                        col = 0;
                        row++;
                    }
                }

                //Lese die Startpostionen ein
                line = br.readLine();
                if(!line.equals("STARTPOINT")){
                    System.out.println("Es gibt einen Fehler bei den Startpositionen im ConfigFile");
                }else{
                    int position = 1; //Beginnt hier zu zaehlen
                    while (true) {
                        line = br.readLine();
                        if (line == "" || line == null || line.equals("CHECKPOINT")) {
                            break;
                        }
                        String numbers[] = line.split(" ");
                        Object object[] = new Object[numbers.length];
                        for (int i = 0; i < 2; i++) {
                            object[i] = (double) Integer.parseInt(numbers[i]);
                        }

                        object[2] = Integer.parseInt(numbers[2]);

                        if (object[2] == (Integer) 1) {
                            object[2] = "up";
                        }
                        if (object[2] == (Integer) 2) {
                            object[2] = "down";
                        }
                        if (object[2] == (Integer) 3) {
                            object[2] = "left";
                        }
                        if (object[2] == (Integer) 4) {
                            object[2] = "right";
                        }

                        //Eingeben in das StartPositionenApp
                        if (startpositionenArray == null) {
                            startpositionenArray = new HashMap<Integer, Object[]>();
                        }
                        startpositionenArray.put(position, object);
                        position++;
                    }
                }

            //Einlesen der CHECKPUNKTE
            if(!line.equals("CHECKPOINT")){
                System.out.println("Es gibt einen Fehler bei den CKECkPOINTS im ConfigFile");
            }else {
                checkpointListe = new HashMap<Integer, List<TileKoordinate>>();
                //String filePath = checkpoints;
                int maxCheckpoint = 0;
                while (true) {

                    line = br.readLine();
                    if (line == "" || line == null) {
                        break;
                    }
                    String numbers[] = line.split(" ");
                    int intnumbers[] = new int[numbers.length];
                    for (int i = 0; i < numbers.length; i++) { //Lese das Array aus in ein Int Array
                        intnumbers[i] = Integer.parseInt(numbers[i]);
                    }
                    //Schaffe Liste und setzte checkpoints
                    if (!checkpointListe.containsKey(intnumbers[2])) {
                        List<TileKoordinate> checkList = new ArrayList<>();
                        checkList.add(new TileKoordinate(intnumbers[0], intnumbers[1]));
                        checkpointListe.put(intnumbers[2], checkList);
                    } else {
                        //Fuege Element der Checkpointliste hinzu
                        List<TileKoordinate> checkList = checkpointListe.get(intnumbers[2]);
                        checkList.add(new TileKoordinate(intnumbers[0], intnumbers[1]));
                        checkpointListe.put(intnumbers[2], checkList);
                    }
                    //mapTiles[intnumbers[0]-1][intnumbers[1]-1].Checkpoint = intnumbers[2]; //Setzte die Checkpoints
                    maxCheckpoint = intnumbers[2]; //Setzte den maximalen Checkpoint
                    }
                    this.maxCheckpoint = maxCheckpoint;

            }

            br.close();

        }catch (IOException e) {
        e.printStackTrace();
    }

    }

    /**
     * Lade alle Auto Bilder
     */
    public void ladeAutoBilder() {
        autoTiles = new HashMap<Integer, Map<Integer, BufferedImage>>();
        try {
            //RotesAuto
            Object[] object = autoListe.get("RotesAuto");
            int id = (int) object[0];
            Map<Integer, BufferedImage> autoMap = new HashMap<Integer, BufferedImage>();

            String filePath = new File("").getAbsolutePath();
            System.out.println(filePath + "/Res/Player/Auto sprites/RotAutoup.png");

            //ID Up = 1, down = 2, left = 3, right = 4;
            autoMap.put(1, ImageIO.read(getClass().getResource("/Player/AutoSprites/RotAutoup.png")));
            autoMap.put(2, ImageIO.read(getClass().getResource("/Player/AutoSprites/RotAutodown.png")));
            autoMap.put(3, ImageIO.read(getClass().getResource("/Player/AutoSprites/RotAutoleft.png")));
            autoMap.put(4, ImageIO.read(getClass().getResource("/Player/AutoSprites/RotAutoright.png")));
            //Map Hinzufuegen
            autoTiles.put(id, autoMap);

            //BlauesAuto
            object = autoListe.get("BlauesAuto");
            id = (int) object[0];
            autoMap = new HashMap<Integer, BufferedImage>();
            //ID Up = 1, down = 2, left = 3, right = 4;
            autoMap.put(1, ImageIO.read(getClass().getResource("/Player/AutoSprites/BlauAutoup.png")));
            autoMap.put(2, ImageIO.read(getClass().getResource("/Player/AutoSprites/BlauAutodown.png")));
            autoMap.put(3, ImageIO.read(getClass().getResource("/Player/AutoSprites/BlauAutoleft.png")));
            autoMap.put(4, ImageIO.read(getClass().getResource("/Player/AutoSprites/BlauAutoright.png")));
            //Map Hinzufuegen
            autoTiles.put(id, autoMap);

            //GelbesAuto
            object = autoListe.get("GelbesAuto");
            id = (int) object[0];
            autoMap = new HashMap<Integer, BufferedImage>();
            //ID Up = 1, down = 2, left = 3, right = 4;
            autoMap.put(1, ImageIO.read(getClass().getResource("/Player/AutoSprites/GelbAutoup.png")));
            autoMap.put(2, ImageIO.read(getClass().getResource("/Player/AutoSprites/GelbAutodown.png")));
            autoMap.put(3, ImageIO.read(getClass().getResource("/Player/AutoSprites/GelbAutoleft.png")));
            autoMap.put(4, ImageIO.read(getClass().getResource("/Player/AutoSprites/GelbAutoright.png")));
            //Map Hinzufuegen
            autoTiles.put(id, autoMap);

            //GruenesAuto
            object = autoListe.get("GruenesAuto");
            id = (int) object[0];
            autoMap = new HashMap<Integer, BufferedImage>();
            //ID Up = 1, down = 2, left = 3, right = 4;
            autoMap.put(1, ImageIO.read(getClass().getResource("/Player/AutoSprites/GruenesAutoup.png")));
            autoMap.put(2, ImageIO.read(getClass().getResource("/Player/AutoSprites/GruenesAutodown.png")));
            autoMap.put(3, ImageIO.read(getClass().getResource("/Player/AutoSprites/GruenesAutoleft.png")));
            autoMap.put(4, ImageIO.read(getClass().getResource("/Player/AutoSprites/GruenesAutoright.png")));
            //Map Hinzufuegen
            autoTiles.put(id, autoMap);

            //System.out.println(autoTiles);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Lese die JSON-Auto-Liste aus und erstelle ein HashMap mit allen Autotypen sowie deren Eigenschaften
     */
    public void macheAutoListe() {

        autoListe = new HashMap<String, Object[]>();

        JSONParser jsonParser = new JSONParser();
        try {
            String filePath = new File("").getAbsolutePath();
            //System.out.println (filePath);
            //Parsing the contents of the JSON file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(filePath + autoDatenDatei));
            //get die Autos
            JSONArray ja = (JSONArray) jsonObject.get("auto");
            //iterator autos
            Iterator itr = ja.iterator();
            //Iterieren ueber die Autos
            int i = 0;
            while (itr.hasNext()) {
                Object o = ja.get(i);
                JSONObject object = (JSONObject) o;

                int autoId = Integer.parseInt(object.get("autoId").toString());

                String datenkomponente = object.get("datenkomponente").toString();
                String physikkomponente = object.get("physikkomponente").toString();

                int masse = Integer.parseInt(object.get("masse").toString());
                int maxGeschwindigkeit = Integer.parseInt(object.get("maxGeschwindigkeit").toString());
                int fahrKraftsteigerung = Integer.parseInt(object.get("fahrKraftsteigerung").toString());

                //System.out.println(autoId + datenkomponente + physikkomponente + masse +  maxGeschwindigkeit + fahrKraftsteigerung);

                Object[] daten = new Object[]{autoId, datenkomponente, physikkomponente, masse, maxGeschwindigkeit, fahrKraftsteigerung};
                autoListe.put(object.get("autoTyp").toString(), daten);


                //System.out.println(object);
                itr.next();
                i++;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    /**
     * Lese die Tile Bilder ein und strutuiere sie in eine HashMap
     */
    public void ladeTileBilder() {
        spielfeldTiles = new HashMap<Integer, BufferedImage>();

        Map<Integer, Object[]> array = tileInformationen;

        for (Map.Entry<Integer, Object[]> entry : array.entrySet()) {
            Object[] o = entry.getValue();
            String pfad = (String) o[1];
            try {
                spielfeldTiles.put(entry.getKey(), ImageIO.read(getClass().getResource(pfad)));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

//		try {
//			spielfeldTiles.put(9, ImageIO.read(getClass().getResource("/Tiles/Newversion/road00_09.png")));
//			spielfeldTiles.put(10, ImageIO.read(getClass().getResource("/Tiles/Newversion/wall_10.png")));
//			spielfeldTiles.put(11, ImageIO.read(getClass().getResource("/Tiles/Newversion/road00_11.png")));
//			spielfeldTiles.put(12, ImageIO.read(getClass().getResource("/Tiles/Newversion/road01_12.png")));
//			spielfeldTiles.put(13, ImageIO.read(getClass().getResource("/Tiles/Newversion/road02_13.png")));
//			spielfeldTiles.put(14, ImageIO.read(getClass().getResource("/Tiles/Newversion/road03_14.png")));
//			spielfeldTiles.put(15, ImageIO.read(getClass().getResource("/Tiles/Newversion/road04_15.png")));
//			spielfeldTiles.put(16, ImageIO.read(getClass().getResource("/Tiles/Newversion/road05_16.png")));
//			spielfeldTiles.put(17, ImageIO.read(getClass().getResource("/Tiles/Newversion/road06_17.png")));
//			spielfeldTiles.put(18, ImageIO.read(getClass().getResource("/Tiles/Newversion/road07_18.png")));
//			spielfeldTiles.put(19, ImageIO.read(getClass().getResource("/Tiles/Newversion/road08_19.png")));
//			spielfeldTiles.put(20, ImageIO.read(getClass().getResource("/Tiles/Newversion/grass01_20.png")));
//			spielfeldTiles.put(21, ImageIO.read(getClass().getResource("/Tiles/Newversion/road09_21.png")));
//			spielfeldTiles.put(22, ImageIO.read(getClass().getResource("/Tiles/Newversion/road10_22.png")));
//			spielfeldTiles.put(23, ImageIO.read(getClass().getResource("/Tiles/Newversion/road11_23.png")));
//			spielfeldTiles.put(24, ImageIO.read(getClass().getResource("/Tiles/Newversion/road12_24.png")));
//			spielfeldTiles.put(30, ImageIO.read(getClass().getResource("/Tiles/Newversion/water01_30.png")));
//			spielfeldTiles.put(31, ImageIO.read(getClass().getResource("/Tiles/Newversion/water02_31.png")));
//			spielfeldTiles.put(32, ImageIO.read(getClass().getResource("/Tiles/Newversion/water03_32.png")));
//			spielfeldTiles.put(33, ImageIO.read(getClass().getResource("/Tiles/Newversion/water04_33.png")));
//			spielfeldTiles.put(34, ImageIO.read(getClass().getResource("/Tiles/Newversion/water05_34.png")));
//			spielfeldTiles.put(35, ImageIO.read(getClass().getResource("/Tiles/Newversion/water06_35.png")));
//			spielfeldTiles.put(36, ImageIO.read(getClass().getResource("/Tiles/Newversion/water07_36.png")));
//			spielfeldTiles.put(37, ImageIO.read(getClass().getResource("/Tiles/Newversion/water08_37.png")));
//			spielfeldTiles.put(38, ImageIO.read(getClass().getResource("/Tiles/Newversion/water09_38.png")));
//			spielfeldTiles.put(39, ImageIO.read(getClass().getResource("/Tiles/Newversion/water10_39.png")));
//			spielfeldTiles.put(40, ImageIO.read(getClass().getResource("/Tiles/Newversion/water11_40.png")));
//			spielfeldTiles.put(41, ImageIO.read(getClass().getResource("/Tiles/Newversion/water12_41.png")));
//			spielfeldTiles.put(42, ImageIO.read(getClass().getResource("/Tiles/Newversion/water13_42.png")));
//			spielfeldTiles.put(45, ImageIO.read(getClass().getResource("/Tiles/Newversion/Ziel00_45.png")));
//			spielfeldTiles.put(46, ImageIO.read(getClass().getResource("/Tiles/Newversion/Ziel01_46.png")));
//			spielfeldTiles.put(47, ImageIO.read(getClass().getResource("/Tiles/Newversion/Ziel02_47.png")));
//			spielfeldTiles.put(50, ImageIO.read(getClass().getResource("/Tiles/Newversion/asphalt00_50.png")));
//			spielfeldTiles.put(51, ImageIO.read(getClass().getResource("/Tiles/Newversion/asphalt01_51.png")));
//			spielfeldTiles.put(52, ImageIO.read(getClass().getResource("/Tiles/Newversion/asphalt02_52.png")));
//			spielfeldTiles.put(53, ImageIO.read(getClass().getResource("/Tiles/Newversion/asphalt03_53.png")));
//			spielfeldTiles.put(54, ImageIO.read(getClass().getResource("/Tiles/Newversion/asphalt04_54.png")));
//			
//			
//		}catch(IOException e) {
//			e.printStackTrace();
//		}
    }

    /**
     * Implementierung des Singelton Patterns, gebe die akteuelle Instanz zurueck
     *
     * @return Instance Spieloptionen
     */
    public static Spieloptionen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Spieloptionen();
        }

        return INSTANCE;
    }

//	 //Einlesen des Config files
//	public void ladeConfigFile(String configFile) {
//		// TODO Auto-generated method stub
//		
//	}

    /**
     * Einlesen des JSON-ConfigFiles, es werden die Parameter gesetzt und die Spieloptionen initialisert
     *
     * @param impdef Config-File-Pfad
     */
    public void setSpieloptionen(String impdef) {
        JSONParser jsonParser = new JSONParser();
        try {
            //Parsing the contents of the JSON file
            String filePath = new File("").getAbsolutePath();
            //System.out.println (filePath);


            JSONObject jsonObject;
            try {
                jsonObject =(JSONObject) jsonParser.parse(new FileReader(filePath + impdef));
            }
            //Falls Map nicht verpackt ist und vom Anwender erstellt wurde
            catch(Exception e) {
                filePath = new File(impdef).getParent();
                jsonObject = (JSONObject) jsonParser.parse(new FileReader(impdef));
            }

            //Auslesen der JSONConfigDatei
            int port = Integer.parseInt(jsonObject.get("port").toString());
            this.port = port;
            String hostname = (String) jsonObject.get("hostname");
            this.hostname = hostname;
            int timeout = Integer.parseInt(jsonObject.get("timeout").toString());
            this.timeout = timeout;

            int maxAnzahlSpieler = Integer.parseInt(jsonObject.get("maxAnzahlSpieler").toString());
            this.maxAnzahlSpieler = maxAnzahlSpieler;
            int AnzahlRomoteSpieler = Integer.parseInt(jsonObject.get("AnzahlRomoteSpieler").toString());
            this.AnzahlRomoteSpieler = AnzahlRomoteSpieler;
            int AnzahlKISpieler = Integer.parseInt(jsonObject.get("AnzahlKISpieler").toString());
            this.AnzahlKISpieler = AnzahlKISpieler;
            boolean SpielerAufServer = Boolean.parseBoolean((String) jsonObject.get("SpielerAufServer"));
            this.SpielerAufServer = SpielerAufServer;

            int spiellaenge = Integer.parseInt(jsonObject.get("spiellaenge").toString());
            this.spiellaenge = spiellaenge;

            this.spielmodus = Integer.parseInt(jsonObject.get("spielmodus").toString());

            String spielfeld = (String) jsonObject.get("spielfeld");
            if(!filePath.equals(new File("").getAbsolutePath())) {
                this.spielfeld = filePath + "/" + spielfeld;
            }
            else {
                this.spielfeld = spielfeld;
            }
/*            String checkpoints = (String) jsonObject.get("checkpoints");
            this.checkpoints = checkpoints;
            String startpositionen = (String) jsonObject.get("startpositionen");
            this.startpositionen = startpositionen;*/
            String autoDatenDatei = (String) jsonObject.get("autoDatenDatei");
            this.autoDatenDatei = autoDatenDatei;

            int abprallen = Integer.parseInt(jsonObject.get("abprallen").toString());
            this.abprallen = abprallen;

            //KartenBildPfade//Eigenschaften auslesen
            JSONArray jarray = (JSONArray) jsonObject.get("KartenTile");
            //Mache eine neue HashMap
            tileInformationen = new HashMap<Integer, Object[]>();

            for (int i = 0; i < jarray.size(); i++) {
                JSONObject o = (JSONObject) jarray.get(i);

                Object[] tileWerte = new Object[5];
                long tileNummerLong = (long) o.get("nummer");
                int tileNummerInt = (int) tileNummerLong;

                tileWerte[0] = (String) o.get("name");
                tileWerte[1] = (String) o.get("pfad");
                tileWerte[2] = Double.parseDouble((String) o.get("reibung"));
                tileWerte[3] = Boolean.parseBoolean((String) o.get("collision"));
                tileWerte[4] = Boolean.parseBoolean((String) o.get("ziel"));

                tileInformationen.put(tileNummerInt, tileWerte);
            }

            System.out.println("Config File Gefunden und Daten wurden eingelesen");

            SpieloptionenInitialisieren();


        } catch (FileNotFoundException e) {
            System.out.println("Config File Nicht Gefunden!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    /**
     * Methode die das Intialisieren der Spieloptionen in der richtigen Reihenfolge verwaltet
     */
    private void SpieloptionenInitialisieren() {
        //Vorerst
        LadeKarteInformation();

        macheAutoListe();
        ladeAutoBilder();
        ladeTileBilder();
        }


    public void setClientSpieloptionen() {

        client = true; //wird als Client Initialisiert

        if (CLI.getPort() != null) {
            this.port = CLI.getPort();
        } else {
            System.out.println("Es wird der default Port: " + port + " verwendet");
        }

        if (CLI.getIP() != null) {
            this.hostname = CLI.getIP();
        } else {
            System.out.println("Es wird der default hostname:" + hostname + " verwendet");
        }

    }

    static final Logger logger = LogManager.getLogger();

    public int werteErgebnisseAus(Object[] finaleSpieldaten, boolean print) {
        //Spielmodus 1: es ist nur beste Zeit/Runde wichtig
        int add = 2;
        //Spielmodus 2: es ist nur wichtig, drei Runden zu fahren
        if (spielmodus == 2) {
            add = 1;
        }

        double min = -1000;
        int sieger = 0;
        for (int j = 0; j < finaleSpieldaten.length; j += 3) {
            if(finaleSpieldaten[j] == null)
                break;

            if(print) {
                logger.info("   Spieler " + finaleSpieldaten[j] +
                        "\n     Runden: " + finaleSpieldaten[j + 1] +
                        "\n     Beste Zeit/Runde: " + finaleSpieldaten[j + 2]);
            }
            else {
                //Spielmodus 1: Beste Rundenzeit
                if (spielmodus == 1) {
                    if ((double) finaleSpieldaten[j + add] > min) {
                        min = (double) finaleSpieldaten[j + add];
                        sieger = (int) finaleSpieldaten[j];
                    }
                }
                //Spielmodus 2: Drei Runden
                if (spielmodus == 2) {
                    if ((int) finaleSpieldaten[j + add] == 3) {
                        sieger = (int) finaleSpieldaten[j];
                        break;
                    }
                }
            }
        }

        return sieger;
    }

    public int getSieger() {
        return sieger;
    }
}

