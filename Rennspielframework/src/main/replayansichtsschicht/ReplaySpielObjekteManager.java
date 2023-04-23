package replayansichtsschicht;

import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;

/**
 * Stellt den globalen Ansprechpartner der Replay-Clients dar.
 * Fuehrt das update-event aus
 */
public class ReplaySpielObjekteManager implements EventListener {
    /**
     * Verwaltet eingehende Events
     */
    EventManager event;

    /**
     * Stellt die globalen Spielinformationen dar
     */
    Spieloptionen optionen;

    /**
     * Aktuelle Richtung des Spielers
     */
    String direction;
    /**
     * Aktuelle Position des Spielers
     */
    int posX, posY;

    /**
     * Instanziiert einen Replay-Client
     */
    ReplaySpielObjekteManager() {
        optionen = Spieloptionen.getInstance();
        event = EventManager.getInstance();
        event.subscribe("update_koordinate", this);
        event.subscribe("aendere_status_beendet", this);
        direction = null;
    }

    @Override
    public void updateEvent(String eventType, Object... eventData) {
        if (eventType == "update_koordinate") {
            if ((int) eventData[0] == optionen.spielerID) {
                direction = eventData[1].toString();
                posX = (int) eventData[2];
                posY = (int) eventData[3];
            }
        }
        if (eventType == "aendere_status_beendet") {
            Object[] finaleSpieldaten = (Object[]) eventData[1];
            int sieger = (int) finaleSpieldaten[finaleSpieldaten.length - 1];
            System.out.println("Ergebnisse:");
            optionen.werteErgebnisseAus(finaleSpieldaten, true);

            if (optionen.spielerID == sieger) {
                System.out.println("Ich habe gewonnen!");
            } else {
                System.out.println("Ich habe verloren.");
            }

            System.exit(0);
        }
    }
}
