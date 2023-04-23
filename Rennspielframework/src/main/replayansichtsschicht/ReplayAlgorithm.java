package replayansichtsschicht;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fuehrt ein bereits gefahrenes Spiel nochmal aus.
 * Hierfuer muss eine Konfigurationsdatei mit den notwendigen Timestamps vohanden sein.
 */
public class ReplayAlgorithm {
    static final Logger logger = LogManager.getLogger();

    /**
     * Stellt den globalen Ansprechpartner der Replay-Clients dar.
     */
    ReplaySpielObjekteManager manager;

    /**
     * Richtungen die an Server zurückgesendet werden
     */
    boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Beinhaltet alle Richtungen (Index 0) und Timestamps (Index 1) der Wiederholung
     */
    List<String[]> protokoll;

    /**
     * Pfad zur Konfigurationsdatei, die die Timestamps beinhaltet
     */
    String configFile;

    /**
     * Zeit der aktuellen Richtungsentscheidung
     */
    long endTime;

    static boolean verbose = true;

    long nowL;

    /**
     * Instantiiert den Replay-Algorithmus
     *
     * @param config Pfad zur Konfigurationsdatei
     */
    ReplayAlgorithm(String config) {
        nowL = Calendar.getInstance().getTimeInMillis();

        protokoll = new ArrayList<>();
        configFile = config;
        resetPressed();
        endTime = 0;
        manager = new ReplaySpielObjekteManager();
    }

    /**
     * Gibt eine Ausgabe aus, falls verbose=true
     *
     * @param string Auszugebende Nachricht
     */
    private void Log(String string) {
        if (verbose) {
            logger.info("---Replay: " + string);
        }
    }

    /**
     * Liest die Konfigurationsdatei und parst in das Protokoll als Liste
     */
    void readConfig() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        jsonObject = (JSONObject) jsonParser.parse(new FileReader(configFile));


        Log("Reading config... " + configFile);
        for (var entry : (ArrayList<JSONObject>) jsonObject.get("timestamps")) {
            String direction = (String) entry.get("direction");
            protokoll.add(new String[]{direction, (String) entry.get("timestamp")});
        }
    }

    /**
     * Setze alle Richtungen zurueck
     */
    public void resetPressed() {
        upPressed = downPressed = leftPressed = rightPressed = false;
    }

    /**
     * Richtung, die an Server zurückgesendet werden soll, wird gepresst
     *
     * @param richtung in die zu fahrende Richtung
     */
    public void pressRichtung(String richtung) {
        resetPressed();
        if (richtung.equals("left"))
            leftPressed = true;
        else if (richtung.equals("right"))
            rightPressed = true;
        else if (richtung.equals("up"))
            upPressed = true;
        else if (richtung.equals("down"))
            downPressed = true;
        else if (richtung.equals("up+left")) {
            upPressed = true;
            leftPressed = true;
        } else if (richtung.equals("down+right")) {
            downPressed = true;
            rightPressed = true;
        } else if (richtung.equals("up+right")) {
            upPressed = true;
            rightPressed = true;
        } else if (richtung.equals("down+left")) {
            downPressed = true;
            leftPressed = true;
        }
    }

    /**
     * Setzt den Member endTime mit den angegebenen Start- und EndStrings
     *
     * @param startString Start des Timestamps
     * @param endString   Ende des Timestamps
     */
    void setNewTimer(String startString, String endString) {
        long start = Long.parseLong(startString);
        long end = Long.parseLong(endString);


        long diffInNanoSeconds = Math.abs(end - start);
        //endTime = Calendar.getInstance();
        //endTime.add(Calendar, (int) diffInMilliSeconds);
        endTime += diffInNanoSeconds;
    }

    /**
     * Arbeitet das Protokoll weiter ab. Ist die Zeit noch nicht abgelaufen, so wird nichts gemacht.
     * Das Protokoll betrachtet hierbei nur die ersten beiden Eintraege und loescht anschliessen den ersten Eintrag.
     */
    void ermittleRichtung() {
        //Endzeit noch nicht erreicht
        if (endTime == 0) {
            endTime = System.nanoTime();
        }

        nowL = System.nanoTime();
        //Log("Next: " + endTimeL);
        //Log("Now : " + nowL);
        //Ende noch nicht erreicht
        if (nowL < endTime) {
            return;
        }

        String richtung = protokoll.get(0)[0];
        Log("Pressing " + richtung);
        pressRichtung(richtung);
        setNewTimer(protokoll.get(0)[1], protokoll.get(1)[1]);
        protokoll.remove(0);

        Log("New time: " + endTime);

    }


    /**
     * Senden der Richtung an Server
     */
    public void schickeRichtung() {
        manager.event.notify("key_event", manager.optionen.spielerID, upPressed, downPressed, leftPressed, rightPressed);
    }

    /**
     * Ermittelt Richtung und schickt diese als Event
     */
    public void update() throws IOException, ParseException {
        if (protokoll.size() == 0) {
            readConfig();
        }

        if (manager.direction == null) {
            return;
        }
        ermittleRichtung();
        schickeRichtung();
    }
}
