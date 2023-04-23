package kiansichtsschicht;

import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;
import spielansichtsschicht.*;
import spiellogikschicht.Spielstadien;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Stellt den globalen Ansprechpartner der KIs dar.
 * Fuehrt das update-event aus
 */
public class KISpielObjekteManager implements EventListener {
    /**
     * Verwaltet eingehende Events
     */
    EventManager event;

    /**
     * Stellt die globalen Spielinformationen dar
     */
    public Spieloptionen optionen;

    /**
     * Aktuelle Richtung des Spielers
     */
    String direction;
    /**
     * Aktuelle Position des Spielers
     */
    int posX, posY;

    /**
     * Erzeugt KISpielObjekteManager
     */
    public KISpielObjekteManager() {
        optionen = Spieloptionen.getInstance();
        event = EventManager.getInstance();
        event.subscribe("update_koordinate", this);
        event.subscribe("aendere_status_beendet", this);
        direction = null;
    }

    /**
     * Anzahl an Zeitmessungen, die durchgefuert wurden
     */
    int timeCounter = 0;
    /**
     * Zeit der aktuellen Messung
     */
    long timeGesamt = 0;
    /**
     * Startzeit der aktuellen Messung
     */
    long startTime = 0;

    /**
     * Startet den Zeitmessvorgang
     */
    public void zeitMessungStart() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Beendet den Zeitmessvorgang
     */
    public void zeitMessungEnde() {
        long ende = System.currentTimeMillis() - startTime;
        timeCounter++;
        timeGesamt += ende;
    }

    /**
     * Berechnet den Durchschnitt der gemessenen Zeiten
     * @return Durchschnitt der gemessenen Zeiten
     */
    public long zeitMessungDurchschnitt() {
        return timeGesamt / (long)timeCounter;
    }

    /**
     * Liefert die aktuelle Richtung des Spielers
     * @return die aktuelle Richtung des Spielers
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Liefert die aktuelle Spaltenposition des Spielers
     * @return die aktuelle Spaltenposition des Spielers
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Liefert die aktuelle Zeilenposition des Spielers
     * @return die aktuelle Zeilenposition des Spielers
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Wandelt die eingegebenen Positionen in die korresponierende Koordinate der Kachel um
     * @param posX Spaltenposition
     * @param posY Zeilenposition
     * @return korresponierende Koordinate der Kachel
     */
    public TileKoordinate getTileKoordinate(double posX, double posY) {
        Spieloptionen optionen = Spieloptionen.getInstance();

        //Ermittle Mittlere Auto position
        int akteurMitteX = (int)(posX + optionen.tileGroesse/2);
        int akteurMitteY = (int)(posY + optionen.tileGroesse/2);
        int akteurTileMitteX = akteurMitteX / optionen.tileGroesse;
        int akteurTileMitteY = akteurMitteY / optionen.tileGroesse;

        return new TileKoordinate(akteurTileMitteX, akteurTileMitteY);
    }

    /**
     * Liefert den Tiletypen (ID) der angegebenen Position
     * @param posX Spaltenposition
     * @param posY Zeilenposition
     * @return Korrespondierende Tile-ID
     */
    public int getTileTypVonPosition(double posX, double posY) {
        Spieloptionen optionen = Spieloptionen.getInstance();

        TileKoordinate tile = getTileKoordinate(posX, posY);

        if(tile.getTileY() < 0 || tile.getTileX() < 0
            || tile.getTileX() >= 30 || tile.getTileY() >= 20)
            return -1;

        return optionen.mapTileNum[tile.getTileX()][tile.getTileY()];
    }

    @Override
    public void updateEvent(String eventType, Object... eventData) {
        if(eventType =="update_koordinate" ) {
            if((int)eventData[0] == optionen.spielerID) {
                direction = eventData[1].toString();
                posX = (int) eventData[2];
                posY = (int) eventData[3];
            }
        }
        if(eventType == "aendere_status_beendet") {
            Object[] finaleSpieldaten = (Object[]) eventData[1];
            int sieger = (int)finaleSpieldaten[finaleSpieldaten.length-1];
            System.out.println("Ergebnisse:");
            optionen.werteErgebnisseAus(finaleSpieldaten, true);

            if(optionen.spielerID == sieger) {
                System.out.println("Ich habe gewonnen!");
            }
            else {
                System.out.println("Ich habe verloren.");
            }

            System.exit(0);
        }
    }
}
