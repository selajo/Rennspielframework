package kiansichtsschicht;

import anwendungsschicht.TileKoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;

/**
 * Interface für die KIs.
 */
public abstract class AKI {

    /**
     * Berechne zu fahrende Richtung und schicke Richtung an Server.
     */

    static final Logger logger = LogManager.getLogger(AKI.class);

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean zurueckAufStrasse;
    public boolean checkPointPassiert;
    public boolean habeGebremst = false;

    public int saveMapTileX, saveMapTileY;

    public String richtung = null;
    public String saveDirection;

    public SpielGraph spielGraph;
    public KISpielObjekteManager manager;


    public SpielKnoten StartKnoten; //Eigene Position
    public SpielKnoten ZielKnoten; //Nächster Checkpoint in der Liste
    public SpielKnoten SpielKnotenSpeicher;
    public SpielKnoten nächsterSpielKnoten;
    public SpielKnoten aktuellerSpielKnoten;

    public ArrayList<SpielKnoten> Strecke;

    public TileKoordinate tileKoordinate;

    int checkPointCounter = 1;

    CheckpointManager checkpointManager;

    Map<Integer, List<TileKoordinate>> checkPunktMap;

    boolean kollidiert;

    boolean rundeBeendet;



    public abstract void update();



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

        System.out.println(StartKnoten);
        System.out.println(ZielKnoten);

        return (StartKnoten != null) && (ZielKnoten != null);

    }


    public void graphAlgorithmusRichtungsentscheidung(){

        // Strecke kommt vom Algortihmus und muss noch umgedreht werden
        Collections.reverse(Strecke);

        spielGraph.printGraph(Strecke);// optional

        // Der Nächste angepeilte Knoten ist der vorletzte des Streckenarrays
        //SpielKnoten nächsterSpielKnoten = new SpielKnoten(0,0);
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
                //System.out.println(SpielKnotenSpeicher.getParent());
                if (SpielKnotenSpeicher.getParent() != null) {
                    if (SpielKnotenSpeicher.getParent().getDirection() != null) {
                        //System.out.println(SpielKnotenSpeicher.getParent().getDirection());
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


    public void resetPressed() {
        // nothing pressed
        upPressed = downPressed = leftPressed = rightPressed = false;
    }

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

    public void schickeRichtung() {
        manager.event.notify("key_event", 2, upPressed, downPressed, leftPressed, rightPressed);
    }

    /*
    public void checkPointPassiertCheck(){
        // Funktion soll ermitteln ob ein CheckPoint passiert ist um damit ein umdrehen des Clients zu verhindern
        if(richtung == "left" && saveMapTileX <= ZielKnoten.tileX )
            checkPointPassiert = true;
        else if(richtung == "up" && saveMapTileY <= ZielKnoten.tileY )
            checkPointPassiert = true;
        else if(richtung == "right" && saveMapTileX >= ZielKnoten.tileX )
            checkPointPassiert = true;
        else if(richtung == "down" && saveMapTileY >= ZielKnoten.tileY )
            checkPointPassiert = true;
        else if(richtung == "up+right" && saveMapTileX >= ZielKnoten.tileX )
            checkPointPassiert = true;
        else if(richtung == "up+left" && saveMapTileX <= ZielKnoten.tileX )
            checkPointPassiert = true;
        else if(richtung == "down+right" && saveMapTileX >= ZielKnoten.tileX )
            checkPointPassiert = true;
        else if(richtung == "down+left" && saveMapTileX <= ZielKnoten.tileX)
            checkPointPassiert = true;
       }

       */
       public void checkPointPassiertCheck(Map<Integer, List<TileKoordinate>> checkPointMap){

        //Map<Integer, List<TileKoordinate>> checkPointMap = spielGraph.checkPoints;

        List nextCheckPointList = checkPointMap.get(checkPointCounter);

        if (nextCheckPointList != null) {
            for (int i = 0; i < nextCheckPointList.size(); i++){
                TileKoordinate tileKoordinate = (TileKoordinate) nextCheckPointList.get(i);
                if (tileKoordinate.getTileX() == saveMapTileX && tileKoordinate.getTileY() == saveMapTileY )
                    checkPointPassiert = true;
                break;
            }
        }
    }


    public SpielKnoten ermittleNächstenCheckpoint(Map<Integer, List<TileKoordinate>> checkPointMap){
        //System.out.println(optionen.checkpointListe);
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

    public TileKoordinate randomTileVonCheckpoint(List checkpointList){
        //wähle ein einziges Tile des CheckpointsListe als ZielKnoten
        List checkpointTiles = checkpointList;
        int randomTileIndex = getRandomTileIndex();
        TileKoordinate randomTile = (TileKoordinate) checkpointTiles.get(randomTileIndex);
        return randomTile;
    }

    public int getRandomTileIndex(){
        Random r = new Random();
        int max = 1;
        int min = 0;
        int randInt = r.nextInt(max-min) + min;
        return randInt;
    }



    public void saveMapTile(String richtung){
        TileKoordinate save = manager.getTileKoordinate(manager.posX, manager.posY);

        if (save.getTileX() != saveMapTileX || save.getTileY() != saveMapTileY){
            saveMapTileX = save.getTileX();
            saveMapTileY = save.getTileY();
            saveDirection = richtung;
            //System.out.println("New save: " + saveMapTileX + ", " + saveMapTileY);
        }
        //Wichtig, da sonst die Parents eine Endlosschleife verursachen
        SpielKnoten aktuell = spielGraph.alleSpielKnoten[saveMapTileY][saveMapTileX];
        aktuell.setParent(null);
    }



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


    public String bremseVorKurve(String richtung){
        // wenn "Quergehen" startet, liegt eine Kurve voraus
        if (richtung == "up+left" || richtung == "up+right" || richtung == "down+right" || richtung == "down+left"){
            richtung = null;
            // System.out.println("Ich gehe kurz vom Gas!");
        }
        return richtung;
    }

}
