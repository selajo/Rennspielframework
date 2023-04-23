package kiansichtsschicht;

import anwendungsschicht.TileKoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;

/**
 * Interface für die KIs.
 */
public abstract class AKI {

    static Logger logger = LogManager.getLogger(AKI.class);
    /**
     * Richtungen die an Server zurückgesendet werden
     */
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean zurueckAufStrasse;
    /**
     * Löst die Ermittlung des nächsten Checkpoints aus
     */
    public boolean checkPointPassiert;
    /**
     * Zeigt an, ob Kurve voraus liegt und deshalb vom Gas gegangen werden soll
     */
    public boolean habeGebremst = false;
    /**
     * Eigene Position
     */
    public int saveMapTileX, saveMapTileY;

    public String richtung = null;
    public String saveDirection;

    public SpielGraph spielGraph;
    /**
     * Stellt Input vom Server zur Verfügung
     */
    public KISpielObjekteManager manager;

    /**
     * StartKnoten ist immer eigene Position des Clients
     */
    public SpielKnoten StartKnoten;
    /**
     * ZielKnoten ist immer nächster ermittelter Checkpoint
     */
    public SpielKnoten ZielKnoten;
    /**
     *  Merke den aktuellen Knoten und die aktuelle Richtung für den Bremsvorgang
     */
    public SpielKnoten SpielKnotenSpeicher;
    /**
     * Relevant für Graphalgorithmus Berechnungen
     */
    public SpielKnoten nächsterSpielKnoten;
    public SpielKnoten aktuellerSpielKnoten;

    /**
     * kürzester errechneter Pfad
     */
    public ArrayList<SpielKnoten> Strecke;

    /**
     * hilft, den richtigen nächsten Checkpoint auszuwählen
     */
    int checkPointCounter = 1;

    CheckpointManager checkpointManager;

    Map<Integer, List<TileKoordinate>> checkPunktMap;

    /**
     * für künftigen Kollisionscheck
     */
    boolean kollidiert;
    /**
     * ist eine Runde erfolgreich abgeschlossen
     */
    boolean rundeBeendet;


    /**
     * Sendet Richtung an Server über 4 boolean Member zurück; wird von jedem Client selbst implementiert
     */
    public abstract void update();

    /**
     * Erst wenn StartUp erfolgreich können die Graphalgorithmen los rechnen
     * @return boolean
     */
    public boolean graphAlgorithmusStartUp(){

        checkPointPassiert = false;

        if(manager.direction == null){
            return false;
        }

        if(checkPunktMap == null){
            checkPunktMap = checkpointManager.ermittleCheckpunkte(4);
            checkpointManager.printCheckpunktMap(checkPunktMap);
            return false;
        }

        if(!checkStrasse(manager.posX, manager.posY)) {
            vomWegAbgekommen();
            return false;
        }

        saveMapTile(manager.direction);

        StartKnoten = spielGraph.alleSpielKnoten[saveMapTileY][saveMapTileX];

        checkPointPassiertCheck(checkPunktMap);


        if(StartKnoten == ZielKnoten || checkPointPassiert){
            ZielKnoten = null;
        }

        if (ZielKnoten == null ){
            ZielKnoten = ermittleNächstenCheckpoint(checkPunktMap);
        }

        if (StartKnoten == null && ZielKnoten == null) {
            logger.error("Kein Start und kein Ziel!");
        }

        return (StartKnoten != null) && (ZielKnoten != null);

    }

    /**
     * aus dem kürzesten ermittelten Pfad wird die Richtung ermittelt, die als nächstes an den Server zurückgesendet werden soll
     */
    public void graphAlgorithmusRichtungsentscheidung(){

        // Strecke kommt vom Algortihmus und muss noch umgedreht werden
        Collections.reverse(Strecke);

        spielGraph.printGraph(Strecke);// optional

        // Der Nächste angepeilte Knoten ist der vorletzte des Streckenarrays
        if (Strecke.size() > 1) {
            nächsterSpielKnoten = Strecke.get(1);
        }else if (Strecke.size() == 1){
            nächsterSpielKnoten = Strecke.get(0);
        }else{
            logger.error("Keine Strecke konnte ermittelt werden!");
        }

        // Hole die aktuelle Position des AStern-Clients
        SpielKnoten aktuellerSpielKnoten = spielGraph.alleSpielKnoten[saveMapTileY][saveMapTileX];

        // Jetzt nochmal Nachbarn berechnen für korrekte Richtungsentscheidung
        aktuellerSpielKnoten.berechneNachbarn(spielGraph.getAsphaltKnoten());

        // übergibt letzten Punkt der Strecke, also nächsten SpielKnoten des errechneten Pfades
        String direction = getDirectionVonSpielKnoten(nächsterSpielKnoten);

        if(!checkStrasse(nächsterSpielKnoten.tileX, nächsterSpielKnoten.tileY)){
            vomWegAbgekommen();
        }

        // Ermittelte Richtungsentscheidung des Algorithmus
        richtung = getRichtungVonDirection(direction);

        //checkKollisionMitWandKnoten();

    }


    /**
     * Wenn Wand-Tile erkannt wird soll in die entgegengesetzte Richtung gesteuert werden
     */
    public void wendeKollisionmitWandAb(){
        zurueckAufStrasse = true;

        TileKoordinate aktuell = manager.getTileKoordinate(manager.posX, manager.posY);

        switch(richtung){
            case "right":
                richtung = "left";
                pressRichtung(richtung);
            case "left":
                richtung = "rigth";
                pressRichtung(richtung);
            case "up":
                richtung = "down";
                pressRichtung(richtung);
            case "down":
                richtung = "up";
                pressRichtung(richtung);
            case "up+left":
                richtung = "down+right";
                pressRichtung(richtung);
            case "up+right":
                richtung = "down+left";
                pressRichtung(richtung);
            case "down+left":
                richtung = "up+right";
                pressRichtung(richtung);
            case "down+right":
                richtung = "up+left";
                pressRichtung(richtung);
        }

    }

    /**
     * umliegende Tiles werden auf Wand-Beschaffenheit geprüft, um Umkehr-Vorgang einzuleiten
     */
    public void checkKollisionMitWandKnoten(){
        SpielKnoten aktuellePosition = spielGraph.alleSpielKnoten[saveMapTileY][saveMapTileX];
        aktuellePosition.berechneNachbarn(spielGraph.getAlleSpielKnotenAlsArrayList());
        for (SpielKnoten sk : aktuellePosition.getNachbarKnoten()){
            switch(sk.getDirection()){
                case "WESTEN":
                    if(Objects.equals(manager.direction, "left") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "NORDWEST":
                    if(Objects.equals(manager.direction, "up+left") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "NORDEN":
                    if(Objects.equals(manager.direction, "up") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "NORDOST":
                    if(Objects.equals(manager.direction, "up+right") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "OSTEN":
                    if(Objects.equals(manager.direction, "right") && !checkStrasse(sk.tileX, sk.tileY)){
                        System.out.println("Ich fahr an die Wand!");
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "SÜDOST":
                    if(Objects.equals(manager.direction, "down+right") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "SÜDEN":
                    if(Objects.equals(manager.direction, "down") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
                case "SÜDWEST":
                    if(Objects.equals(manager.direction, "down+left") && !checkStrasse(sk.tileX, sk.tileY)){
                        wendeKollisionmitWandAb();
                        break;
                    }
            }
        }
    }




    public void graphAlgorithmusBremseVorKurve(){

        if (!habeGebremst){
            if (SpielKnotenSpeicher != null) {
                if (SpielKnotenSpeicher.getParent() != null) {
                    if (SpielKnotenSpeicher.getParent().getDirection() != null) {
                        // nur einmal kurz abbremsen vor Kurve
                        richtung = bremseVorKurve(richtung);
                    }
                }
            }
        }

        if (richtung == null){
            habeGebremst = true;
        }else {
            habeGebremst = false;
        }
    }

    /**
     * immer zum Start notwendig
     */
    public void resetPressed() {
        // nothing pressed
        upPressed = downPressed = leftPressed = rightPressed = false;
    }

    /**
     * Überprüfung ob man von der Strecke abgekommen ist
     */
    public void vomWegAbgekommen() {
        zurueckAufStrasse = true;
        String richtung = saveDirection;
        TileKoordinate aktuell = manager.getTileKoordinate(manager.posX, manager.posY);

        if(saveDirection == "right" || saveDirection == "left") {
            if(saveMapTileY > aktuell.getTileY())
                richtung = "down";
            else if(saveMapTileY < aktuell.getTileY())
                richtung = "up";
            else if (saveMapTileX > aktuell.getTileX())
                richtung = "right";
            else if (saveMapTileX < aktuell.getTileX())
                richtung = "left";
        }
        else {
            if (saveMapTileX > aktuell.getTileX())
                richtung = "right";
            else if (saveMapTileX < aktuell.getTileX())
                richtung = "left";
            else if(saveMapTileY > aktuell.getTileY())
                richtung = "down";
            else if(saveMapTileY < aktuell.getTileY())
                richtung = "up";
        }
        pressRichtung(richtung);
    }

    /**
     * Richtung die an Server zurückgesendet werden soll, wird vorbereitet
     * @param richtung: ermittelt durch graphAlgorithmusRichtungsEntscheidung() (nicht in TrivialeKI)
     */
    public void pressRichtung(String richtung) {
        resetPressed();
        //logger.info("richtung: " + richtung);
        if(richtung == "left")
            leftPressed = true;
        else if(richtung == "right")
            rightPressed = true;
        else if(richtung == "up")
            upPressed = true;
        else if(richtung == "down")
            downPressed = true;
        else if (richtung == "up+left") {
            upPressed = true;
            leftPressed = true;
        } else if (richtung == "down+right") {
            downPressed = true;
            rightPressed = true;
        } else if (richtung == "up+right" ) {
            upPressed = true;
            rightPressed = true;
        } else if (richtung =="down+left") {
            downPressed = true;
            leftPressed = true;
        }
    }

    /**
     * tatsächliches zurück Senden an Server
     */
    public void schickeRichtung() {
        manager.event.notify("key_event", manager.optionen.spielerID, upPressed, downPressed, leftPressed, rightPressed);
    }

    /**
     * überprüft ob ein Checkpoint erreicht wurde
     * @param checkPointMap
     */
    public void checkPointPassiertCheck(Map<Integer, List<TileKoordinate>> checkPointMap){

        List nextCheckPointList = checkPointMap.get(checkPointCounter);

        if (nextCheckPointList != null) {
            for (int i = 0; i < nextCheckPointList.size(); i++){
                TileKoordinate tileKoordinate = (TileKoordinate) nextCheckPointList.get(i);
                if (tileKoordinate.getTileX() == saveMapTileX && tileKoordinate.getTileY() == saveMapTileY ) {
                    checkPointPassiert = true;
                    break;
                }
            }
        }
    }

    /**
     * nächster Checkpoint in der Liste wird ausgemacht und dient dadurch als neuer ZielKnoten
     * @param checkPointMap
     * @return
     */
    public SpielKnoten ermittleNächstenCheckpoint(Map<Integer, List<TileKoordinate>> checkPointMap){

        spielGraph = new SpielGraph();

        if (checkPointCounter == checkPointMap.size() + 1){
            checkPointCounter = 1;
            rundeBeendet = true;
        }else{rundeBeendet = false;}
        for(var checkPoint : checkPointMap.entrySet()){
            TileKoordinate tileKoordinate = null;
            List nächsterCheckpointListe = checkPointMap.get(checkPointCounter);
            tileKoordinate = randomTileVonCheckpoint(nächsterCheckpointListe);
            SpielKnoten nächsterCheckpoint = spielGraph.alleSpielKnoten[tileKoordinate.getTileY()][tileKoordinate.getTileX()];
            checkPointCounter++;
            return nächsterCheckpoint;
        }
        return null;
    }

    /**
     * randomisiertes auswählen eines einzigen Tiles eines Checkpoints/Referenzpunktes
     * @param checkpointList: 1 Checkpoint besteht i.d.R aus zwei Tiles
     * @return
     */
    public TileKoordinate randomTileVonCheckpoint(List checkpointList){
        //wähle ein einziges Tile des CheckpointsListe als ZielKnoten
        List checkpointTiles = checkpointList;
        int randomTileIndex = getRandomTileIndex();
        TileKoordinate randomTile = (TileKoordinate) checkpointTiles.get(randomTileIndex);
        return randomTile;
    }

    /**
     * randomiserter Wert
     * @return integer: 0 oder 1
     */
    public int getRandomTileIndex(){
        Random r = new Random();
        int max = 1;
        int min = 0;
        int randInt = r.nextInt(max-min) + min;
        return randInt;
    }


    /**
     * speichert eigene Position des Clients
     * @param richtung
     */
    public void saveMapTile(String richtung){

        TileKoordinate save = manager.getTileKoordinate(manager.posX, manager.posY);

        if (save.getTileX() != saveMapTileX || save.getTileY() != saveMapTileY){
            saveMapTileX = save.getTileX();
            saveMapTileY = save.getTileY();
            saveDirection = richtung;
        }
        //Wichtig, da sonst die Parents eine Endlosschleife verursachen
        SpielKnoten aktuell = spielGraph.alleSpielKnoten[saveMapTileY][saveMapTileX];
        aktuell.setParent(null);
    }


    /**
     * relevant für vomWegAbgekommen(): überprüft ob eigen Position außerhalb der Rennstrecke liegt
     * @param posX
     * @param posY
     * @return
     */
    public boolean checkStrasse(double posX, double posY) {

        boolean erlaubt = false;

        switch (manager.getTileTypVonPosition(posX, posY)) {
            case 45:
            case 11:
            case 9:
                erlaubt = true;
                break;
            default:
                erlaubt = false;
                break;
        }
        return  erlaubt;
    }

    /**
     * umwandlung der Himmelsrichtung in Richtung die an Server geschickt werden kann
     * @param direction
     * @return
     */
    public String getRichtungVonDirection(String direction){
        switch (direction){
            case "NORDEN":
                return "up";
            case "SÜDEN":
                return "down";
            case "WESTEN":
                return "left";
            case "OSTEN":
                return "right";
            case "NORDWEST":
                return "up+left";
            case "SÜDOST":
                return "down+right";
            case "NORDOST":
                return "up+right";
            case "SÜDWEST":
                return "down+left";
        }
        return direction;
    }


    public String getDirectionVonSpielKnoten(SpielKnoten sk){
        String direction = sk.getDirection();
        return direction;
    }

    /**
     * Richtung null an server gesendet um vor Kurve kurz vom Gas zu gehen
     * @param richtung
     * @return
     */
    public String bremseVorKurve(String richtung){
        // wenn "Quergehen" startet, liegt eine Kurve voraus
        if (richtung == "up+left" || richtung == "up+right" || richtung == "down+right" || richtung == "down+left"){
            richtung = null;
        }
        return richtung;
    }

}
