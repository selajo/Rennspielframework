package kiansichtsschicht;


import anwendungsschicht.TileKoordinate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Faehrt die Strecke ab, ohne kürzeste Wege zu berechnen.
 */
public class TrivialeKI extends AKI {

    /**
     * Dient zum Speichern der zuletzt bekannten sicheren Tile
     */
    public int saveMapTileX, saveMapTileY;
    /**
     * Dient zum Speichern zu zuletzt gefahrenen Richtung
     */
    public String saveDirection;

    /**
     * Flag, das beschreibt, ob Client gerade zuück auf die Strasse fährt
     */
    public boolean zurueckAufStrasse;

    /**
     * Für Log-Zwecke
     */
    private boolean verbose;

    /**
     * Berechnet den naechsten Checkpunkt
     */
    private CheckpointManager checkpointManager;

    /**
     * Aktuelle ID des Checkpunktes
     */
    private int checkpunktId;

    /**
     * Checkpunkt inkl. Nachbarknoten
     */
    private List<TileKoordinate> checkpoint;

    /**
     * Maximaler Sichtradius
     */
    private int maxSteps = 12;

    List<TileKoordinate> oldSaves = new ArrayList<>();

    /**
     * Anzahl an Tiles, nach denen angenommen wird, dass der Client in die falsche Richtung faehrt
     */
    int distanzZuCheckpunkt;
    /**
     * Initialidistanz des Clients
     */
    int initDistanz = maxSteps + 4;

    /**
     * Erzeugt TrivialeKI
     */
    public TrivialeKI() {
        manager = new KISpielObjekteManager();
        zurueckAufStrasse = false;
        resetPressed();
        saveMapTileY = saveMapTileX = 0;
        saveDirection = "";
        verbose = true;
        checkpointManager = new CheckpointManager(manager);
        checkpunktId = 0;
        List<TileKoordinate> checkpoint = null;
        distanzZuCheckpunkt = initDistanz;
    }

    /**
     * Setzt das Verbose-Level
     *
     * @param verbose true: Ausgaben sollen erzeugt werden, false: sonst
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Gibt eine Ausgabe aus, falls verbose=true
     *
     * @param string Auszugebende Nachricht
     */
    private void Log(String string) {
        if (verbose) {
            logger.info("---Triviale KI: " + string);
        }

    }

    /**
     * Speichert die aktuelle Tile inkl. Richtung
     *
     * @param richtung Zu speichernde Richtung
     */
    public void saveMapTile(String richtung) {
        TileKoordinate save = manager.getTileKoordinate(manager.posX, manager.posY);
        if (save.getTileX() != saveMapTileX || save.getTileY() != saveMapTileY) {
            Log("Distanz zu naechstem Checkpunkt: " + distanzZuCheckpunkt);

            saveMapTileX = save.getTileX();
            saveMapTileY = save.getTileY();
            saveDirection = richtung;
            Log("New save: " + saveMapTileX + ", " + saveMapTileY);
            oldSaves.add(new TileKoordinate(saveMapTileX, saveMapTileY, saveDirection));
            distanzZuCheckpunkt--;
        }
    }


    /**
     * Berechnet die Laenge der Strecke nach Richtung.
     *
     * @param richtung Zu pruefende Richtung.
     * @param posX     Zu pruefende X-Position.
     * @param posY     Zu pruefende Y-Position.
     * @return Anzahl halbe Tiles, die Laenge des Wegs beschreiben
     */
    public int pruefeLaengsterWeg(String richtung, double posX, double posY) {
        int counter = 0;
        //Solange akzeptierte Strasse gefunden, zaehle hoch
        while (checkStrasse(posX, posY)) {
            if (richtung == "up")
                posY -= manager.optionen.tileGroesse / 2;
            else if (richtung == "down")
                posY += manager.optionen.tileGroesse / 2;
            else if (richtung == "right")
                posX += manager.optionen.tileGroesse / 2;
            else if (richtung == "left")
                posX -= manager.optionen.tileGroesse / 2;
            counter++;
        }
        return counter;
    }

    /**
     * Faehrt auf das letzte gespeicherte Tile zurueck
     */
    public void vomWegAbgekommen() {
        zurueckAufStrasse = true;
        String richtung = saveDirection;
        TileKoordinate aktuell = manager.getTileKoordinate(manager.posX, manager.posY);

        if (saveDirection == "right" || saveDirection == "left") {
            if (saveMapTileY > aktuell.getTileY())
                richtung = "down";
            else if (saveMapTileY < aktuell.getTileY())
                richtung = "up";
            else if (saveMapTileX > aktuell.getTileX())
                richtung = "right";
            else if (saveMapTileX < aktuell.getTileX())
                richtung = "left";
        } else {
            if (saveMapTileX > aktuell.getTileX())
                richtung = "right";
            else if (saveMapTileX < aktuell.getTileX())
                richtung = "left";
            else if (saveMapTileY > aktuell.getTileY())
                richtung = "down";
            else if (saveMapTileY < aktuell.getTileY())
                richtung = "up";
        }
        pressRichtung(richtung);
        Log("Letzer save: " + saveMapTileX + ", " + saveMapTileY + " " + saveDirection);
        Log("Bin auf: " + aktuell.getTileX() + ", " + aktuell.getTileY());
        Log("Bin weg nach ... " + richtung);
    }

    /**
     * Prueft, ob die aktuellen Koordinaten in dem Bereich (Umkreisgroesse 1) eines Checkpunktes sind.
     *
     * @return True: aktuellen Koordinaten sind im Bereich, False: andernfalls
     */
    boolean isCheckpoint() {
        TileKoordinate current = manager.getTileKoordinate(manager.posX, manager.posY);
        return current.isInArea(checkpoint, 1);
    }

    /**
     * Prueft, ob die aktuelle Tile einen Checkpunkt darstellt.
     * Falls ja, wird der neue Checkpunkt via CheckpointManager ermittelt.
     * Falls noch kein naechster Checkpunkt bekannt ist, wird der naechste Checkpukt anhand der aktuellen
     * Koordinaten ermittelt.
     */
    void pruefeCheckpunkt() {
        //Es gibt noch keinen naechsten Checkpunkt
        if (checkpoint == null) {
            checkpoint = checkpointManager.nextCheckpunkt(checkpunktId, maxSteps);
            Log("Neuer Checkpunkt: " + checkpoint.get(0));
        }
        //Der aktuelle Checkpunkt ist erreicht und es wird der naechste benoetigt
        else if (isCheckpoint()) {
            checkpunktId += 1;
            checkpoint = checkpointManager.nextCheckpunkt(checkpunktId, maxSteps);
            Log("Neuer Checkpunkt: " + checkpoint.get(0));
            distanzZuCheckpunkt = initDistanz;
        }
    }

    /**
     * Prueft, in welcher Richtung, in Kombination zur aktuellen Richtung, der naechste Checkpunkt liegt und
     * gibt die dementsprechende Richtung zurueck.
     *
     * @return Richtung, in die der naechste Checkpunkt liegt.
     */
    String orientierenAnCheckpunkt() {
        TileKoordinate checkpkt = checkpoint.get(checkpoint.size() - 1);
        if (manager.direction.equals("up") || manager.direction.equals("down")) {
            if (checkpkt.getTileX() >= manager.getTileKoordinate(manager.posX, manager.getPosY()).getTileX()) {
                return "right";
            } else {
                return "left";
            }
        } else {
            if (checkpkt.getTileY() >= manager.getTileKoordinate(manager.posX, manager.getPosY()).getTileY()) {
                return "down";
            } else {
                return "up";
            }
        }
    }


    /**
     * Ermittelt eine 180° Drehung zur angegebenen Richtung.
     * @param richtung Zu drehende Richtung.
     * @return Gegenrichtung zur angegebenen Richtung.
     */
    String gegenRichtung(String richtung) {
        if (richtung.equals("up"))
            return "down";
        else if (richtung.equals("down"))
            return "up";
        else if (richtung.equals("left"))
            return "right";
        else
            return "left";
    }

    /**
     * Grosser Prozess, der die naechste zu fahrende Richtung ausgehend von der
     * aktuellen Position ermittelt.
     */
    public void ermittleRichtung() {
        String richtung;
        //Manager hat noch keine Daten bekommen -> noch nicht anfangen
        if (manager.direction == null) {
            return;
        }

        //Ziel ist erreicht
        if (manager.getTileTypVonPosition(manager.getPosX(), manager.getPosY()) == 45) {
            oldSaves = new ArrayList<>();
        }

        pruefeCheckpunkt();

        // Bin ich vom Weg abgekommen?
        if (!checkStrasse(manager.posX, manager.posY)) {
            vomWegAbgekommen();
            Log("Bin abgekommen");
            return;
        }
        // Ich bin wieder auf dem richtigen Weg
        else if (zurueckAufStrasse) {
            pressRichtung(saveDirection);
            zurueckAufStrasse = false;
            Log("Bin back on road: " + saveDirection);
            return;
        }

        int counter = pruefeLaengsterWeg(manager.direction, manager.posX, manager.posY);

        //Distanz zum Checkpunkt ist nicht eingehalten worden -> falsche Richtung
        if (distanzZuCheckpunkt <= 0) {
            Log("ich fahre in die falsche Richtung!");
            richtung = gegenRichtung(manager.direction);
            pressRichtung(richtung);
            distanzZuCheckpunkt = 2 * initDistanz + 2;
        }
        //Solange auf normaler Straße gefahren wird, nichts an Richtung ändern
        else if (counter > manager.optionen.vergroesserung + 1) {
            pressRichtung(manager.direction);
            saveMapTile(manager.direction);
        }
        //Wuerde von Strasse abkommen -> neue Richtung berechnen
        else {
            Log("Reached end of road... " + manager.direction + " Counter: " + counter);
            richtung = orientierenAnCheckpunkt();
            Log("Neue Richtung -> " + richtung);
            pressRichtung(richtung);
            saveMapTile(richtung);
        }
    }


    /**
     * Ermittelt Richtung und schicht diese als Event
     */
    @Override
    public void update() {
        ermittleRichtung();
        schickeRichtung();
    }

}
