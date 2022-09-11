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

public class KISpielObjekteManager implements EventListener {

    EventManager event;
    public Spieloptionen optionen;

    String direction;
    int posX, posY;

    public KISpielObjekteManager() {
        optionen = Spieloptionen.getInstance();
        event = EventManager.getInstance();
        event.subscribe("update_koordinate", this);
        event.subscribe("aendere_status_beendet", this);
        direction = null;
    }

    public String getDirection() {
        return direction;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public TileKoordinate getTileKoordinate(double posX, double posY) {
        Spieloptionen optionen = Spieloptionen.getInstance();

        //Ermittle Mittlere Auto position
        int akteurMitteX = (int)(posX + optionen.tileGroesse/2);
        int akteurMitteY = (int)(posY + optionen.tileGroesse/2);
        int akteurTileMitteX = akteurMitteX / optionen.tileGroesse;
        int akteurTileMitteY = akteurMitteY / optionen.tileGroesse;

        return new TileKoordinate(akteurTileMitteX, akteurTileMitteY);
    }


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
            if(eventData[0].equals(2)) {
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
