package kiansichtsschicht;

import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spiellogikschicht.Tile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Verwaltet die Checkpunkte, die die KI setzen kann
 */
public class CheckpointManager {
    /**
     * Beschreibt die Koordianten, mit denen der CheckpointManager arbeitet
     */
    public static class Points implements Comparable {
        /**
         * Spalte des Feldes
         */
        public int x;
        /**
         * Reihe des Feldes
         */
        public int y;
        /**
         * Richtung des Managers
         */
        String richtung;

        /**
         * Erzeugt Points
         * @param x Spalte
         * @param y Reihe
         * @param richtung Richtung
         */
        public Points(int x, int y, String richtung) {
            this.x = x;
            this.y = y;
            this.richtung = richtung;
        }

        @Override
        public String toString() {
            return "(" + this.x + ", " + this.y + ")";
        }

        @Override
        public int compareTo(Object o) {
            Points comp = (Points) o;
            if (this.x == comp.x && this.y == comp.y) return 0;
            else return 1;
        }

        /**
         * Prueft, ob die aktuelle Instanz von Points in der Liste vorkommt
         * @param list Zu pruefende Liste
         * @return True: vorhanden; False: sonst
         */
        public boolean isInArea(List<Points> list) {
            for (Points l : list) {
                if (l.y == this.y && l.x == this.x) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Der umliegende Bereich (mit groesse Delta) wird der Liste hinzugefuegt
         * @param list Zu aendernde Liste
         * @param delta Zu vergroesserndes Delta
         * @return Erweiterte Liste
         */
        public List<Points> attachToList(List<Points> list, int delta) {
            List<Points> toAdd = new ArrayList<>();
            for (Points l : list) {
                logger.info(l.toString());
                toAdd.add(new Points(l.x + delta, l.y, ""));
                toAdd.add(new Points(l.x - delta, l.y, ""));
                toAdd.add(new Points(l.x, l.y + delta, ""));
                toAdd.add(new Points(l.x, l.y - delta, ""));
            }

            return Stream.concat(list.stream(), toAdd.stream()).collect(Collectors.toList());
        }

        /**
         * Prueft, ob im angegebenen Bereich sich die aktuelle Instanz befindet
         * @param list Zu pruefende Liste
         * @param delta Zu pruefende Bereichsgroesse
         * @return true: vorhanden; false: andernfalls
         */
        public boolean isInArea(List<Points> list, int delta) {
            List<Points> extendedList = attachToList(list, delta);
            return isInArea(extendedList);
        }
    }

    /**
     * Fuer das Loggen zustaendig
     */
    static final Logger logger = LogManager.getLogger(AKI.class);

    /**
     * Flag, der beschreibt, ob der CheckpointManager aktuell auf dem Weg zurueck ist.
     */
    boolean goingBack = false;

    /**
     * Wichtig für Spielfeld-Informationen
     */
    Spieloptionen optionen = Spieloptionen.getInstance();

    /**
     * Anzahl der zu setzenden Checkpunkte
     */
    int checkpunktAnzahl;

    /**
     * Anzahl an Checkpunkten, die mit dem dynamischen Ansatz bereits ermittelt wurden
     */
    int checkpunktMaximum = 0;


    /**
     * Wichtig für aktuelle Fahrtrichtung des Autos
     */
    public KISpielObjekteManager manager;

    /**
     * Anzahl, wie oft das Ziel erreicht wurde.
     */
    boolean zielCounter;
    boolean verbose;

    /**
     * Erzeugt eine CheckpointManager-Instanz.
     *
     * @param manager KISpielObjekteManager, der notwendig ist, um die aktuelle Position zu ermitteln.
     */
    CheckpointManager(KISpielObjekteManager manager) {
        this.manager = manager;
        this.zielCounter = false;
        this.verbose = true;
    }

    /**
     * Stellt den aktuellen akzeptierten Pfad dar.
     */
    ArrayList<Points> aktuellerPfad = new ArrayList<>();
    /**
     * Beinhaltet alle bereits erreichten Ecken / Strassenenden des Spielfeldes.
     */
    ArrayList<Points> ecken = new ArrayList<>();

    /**
     * Aktuell gespeicherten Checkpunkte.
     */
    Map<Integer, List<TileKoordinate>> checkpunkte = new HashMap<>();

    /**
     * Prueft, ob die aktuelle Tile-Position eine Strasse, ein Start oder das Ziel ist.
     * Wird hier nicht vom KISpielObjekteManager uebernommen, da sich diese Ueberprueufung auf Tile-Ebene befindet.
     *
     * @param posX X-Koordinate der Tile.
     * @param posY Y-Koordinate der Tile.
     * @return True: akzeptierte Tile, False: andernfalls.
     */
    public boolean checkStrasse(int posX, int posY) {
        boolean erlaubt;
        switch (optionen.mapTileNum[posX][posY]) {
            case 45:
            case 11:
            case 9:
                erlaubt = true;
                break;
            default:
                erlaubt = false;
                break;
        }
        return erlaubt;
    }

    /**
     * Gibt eine Ausgabe aus, falls verbose=true
     *
     * @param string Auszugebende Nachricht
     */
    private void Log(String string) {
        if (verbose) {
            logger.info("---CheckpointManager: " + string);
        }

    }

    /**
     * Prueft die Laenge der Strasse in der aktuellen Richtung.
     *
     * @param richtung Zu pruefende Richtung.
     * @param posX     Zu pruefende X-Koordinate.
     * @param posY     Zu pruefende Y-Koordinate.
     * @return Anzahl Tiles, die noch bis zum Strassenende verfuegbar sind.
     */
    public int pruefeRichtung(String richtung, int posX, int posY) {
        int counter = 0;
        //Solange akzeptierte Strasse gefunden, zaehle hoch
        while (checkStrasse(posX, posY)) {
            if (richtung == "up") posY -= 1;
            else if (richtung == "down") posY += 1;
            else if (richtung == "right") posX += 1;
            else if (richtung == "left") posX -= 1;
            counter++;
        }
        return counter;
    }

    /**
     * Ermittelt eine 180° Drehung zur angegebenen Richtung.
     *
     * @param richtung Zu drehende Richtung.
     * @return Gegenrichtung zur angegebenen Richtung.
     */
    String gegenRichtung(String richtung) {
        if (richtung.equals("up")) return "down";
        else if (richtung.equals("down")) return "up";
        else if (richtung.equals("left")) return "right";
        else return "left";
    }

    /**
     * Aendert die aktuelle Richtung je nach Versuchen.
     * Bei zweitem Versuch wird in die Gegenrichtung zur aktuellen Richtung gedreht,
     *
     * @param richtung              Zu aendernde Richtung.
     * @param bereitsZweiterVersuch War ich hier schon einmal? Wenn ja -> Gegenrichtung
     * @return Geanderte Richtung.
     */
    String aendereRichtung(String richtung, boolean bereitsZweiterVersuch) {
        if (richtung.equals("up") || richtung.equals("down")) {
            if (bereitsZweiterVersuch) {
                return gegenRichtung(richtung);
            }
            return "right";
        } else {
            if (bereitsZweiterVersuch) {
                return gegenRichtung(richtung);
            }
            return "up";
        }

    }

    /**
     * Geht eine Tile in je nach Richtung in die X-Koordinate weiter.
     *
     * @param richtung Aktuelle Richtung.
     * @param posX     Aktuelle X-Koordinate der Tile.
     * @return Geanderte X-Koordinate je nach Richtung.
     */
    int xGeheWeiterInRichtung(String richtung, int posX) {
        if (richtung == "right") posX += 1;
        else if (richtung == "left") posX -= 1;
        return posX;
    }

    /**
     * Geht eine Tile in je nach Richtung in die Y-Koordinate weiter.
     *
     * @param richtung Aktuelle Richtung.
     * @param posY     Aktuelle Y-Koordinate der Tile.
     * @return Geanderte Y-Koordinate je nach Richtung.
     */
    int yGeheWeiterInRichtung(String richtung, int posY) {
        if (richtung == "up") posY -= 1;
        else if (richtung == "down") posY += 1;
        return posY;
    }

    /**
     * Prueft, ob list eine Instanz von Points p enthaelt.
     *
     * @param list Zu pruefende Liste.
     * @param p    Zu pruefender Points.
     * @return True: Liste enthaelt Points p.
     */
    boolean listContains(ArrayList<Points> list, Points p) {
        for (Points i : list) {
            if (i.x == p.x && i.y == p.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Faehrt zur letzten bekannten Ecke zurueck. Dabei wird der aktuelle Pfad angepasst.
     */
    int zurueckZurLetztenEcke() {
        Points letzteEcke = ecken.get(ecken.size() - 1);
        Points letzterPunkt = aktuellerPfad.get(aktuellerPfad.size() - 1);
        int steps = 0;
        //So lange zurueckfahren, bis letzte Ecke erreicht
        while (true) {
            aktuellerPfad.remove(aktuellerPfad.size() - 1);
            steps++;
            if(letzterPunkt.x == letzteEcke.x && letzterPunkt.y == letzteEcke.y) {
                break;
            }
            else if(aktuellerPfad.size() == 0) {
                break;
            }
            letzterPunkt = aktuellerPfad.get(aktuellerPfad.size() - 1);
        }

        return steps;
    }

    /**
     * Prueft, ob die angegebene Tile-Position ein Blocker (Mauer oder Wasser) ist.
     *
     * @param posX Zu pruefende X-Position.
     * @param posY Zu pruefende Y-Position.
     * @return True: Tile stellt Blocker dar; False: Andernfalls.
     */
    boolean checkBlocker(int posX, int posY) {
        boolean blocker;
        switch (optionen.mapTileNum[posX][posY]) {
            case 10:
            case 30:
                blocker = true;
                break;
            default:
                blocker = false;
                break;
        }
        return blocker;
    }

    /**
     * Prueft, ob nach den Grass-Tiles noch ein weiterer Weg existiert.
     * Falls ja, so wird diese Koordinate zureuckgegeben.
     *
     * @param posX     Zu pruefende X-Position.
     * @param posY     Zu pruefende Y-Position.
     * @param richtung Zu pruefende Richtung.
     * @return Falls kein weiterer Weg existiert, eingegebene Koordianten; Ansonsten: neuer Punkt.
     */
    Points ueberGrassGehen(int posX, int posY, String richtung) {
        Points ausgang = new Points(posX, posY, richtung);
        ArrayList<Points> pointsOfInterest = new ArrayList<>();
        int counter = 0;
        while (true) {
            if (richtung == "up") posY -= 1;
            else if (richtung == "down") posY += 1;
            else if (richtung == "right") posX += 1;
            else if (richtung == "left") posX -= 1;

            if (checkBlocker(posX, posY)) {
                return ausgang;
            }

            pointsOfInterest.add(new Points(posX, posY, richtung));

            //Es gibt tatsächlich eine Strasse
            if (checkStrasse(posX, posY)) {
                //aktuellerPfad = (ArrayList<Points>) Stream.concat(aktuellerPfad.stream(), pointsOfInterest.stream())
                //        .collect(Collectors.toList());
                if (counter > 0) {
                    return new Points(posX, posY, richtung);
                } else {
                    return ausgang;
                }
            }
            counter++;
        }
    }

    /**
     * Gibt die aktuell gespeicherten Ecken aus.
     */
    void printEcken() {
        String output = "";
        for (Points ecke : ecken) {
            output += ecke.toString() + " ";
        }
        Log(output);
    }

    /**
     * Ermittelt einen Pfad von Start zu Ziel.
     *
     * @param aktX             Aktuelle X-Koordinate.
     * @param aktY             Aktuelle Y-Koordinate.
     * @param checkpunktAnzahl Anzahl an Checkpunkten, die gesetzt werden sollen.
     */
    void ermittlePfad(int aktX, int aktY, int checkpunktAnzahl, int maxSteps, String irichtung) {
        this.checkpunktAnzahl = checkpunktAnzahl;
        String richtung = irichtung;
        //Startposition speichern
        String deadend = "";

        //Dient zur aktuellen Position des Managers
        int aktuelleX = aktX;
        int aktuelleY = aktY;


        while (true) {
            int laenge = pruefeRichtung(richtung, aktuelleX, aktuelleY);

            //Ich bin im Kreis gelaufen und befinde mich auf einen bereits
            //bekannten Weg
            if (listContains(aktuellerPfad, new Points(aktuelleX, aktuelleY, richtung))) {
                if (optionen.mapTileNum[aktuelleX][aktuelleY] == 9 || optionen.mapTileNum[aktuelleX][aktuelleY] == 45) {

                }
                //Ich bin aktuell in keiner Sackgasse gewesen
                else if (!goingBack) {
                    int delta = zurueckZurLetztenEcke();
                    maxSteps += delta;
                    Points ecke = ecken.get(ecken.size() - 1);
                    aktuelleX = ecke.x;
                    aktuelleY = ecke.y;

                    Log("Reset: " + aktuelleX + " " + aktuelleY + " " + ecke.richtung);
                    deadend = ecke.richtung;
                    goingBack = true;
                } else {
                    Points letzteEcke = ecken.get(ecken.size() - 1);
                    if (letzteEcke.x == aktuelleX && letzteEcke.y == aktuelleY) {
                        goingBack = false;
                        Log("Reached last ecke");
                    }
                    aktuellerPfad.remove(aktuellerPfad.size() - 1);
                }
            }

            Log("Current x y" + aktuelleX + " " + aktuelleY + " laenge " + laenge + " r " + richtung);

            //Ecke ist erreicht, pruefe naechste Richtung
            if (laenge == 1) {
                Log("Ecke erreicht x y" + aktuelleX + " " + aktuelleY);
                printEcken();
                //Pruefe ob ueber Grass gefahren werden muss
                Points pruefung = ueberGrassGehen(aktuelleX, aktuelleY, richtung);
                if (pruefung.x != aktuelleX || pruefung.y != aktuelleY) {
                    Log("Ich habe noch was gefunden!" + pruefung.x + " " + pruefung.y + " " + richtung);
                    aktuelleX = pruefung.x;
                    aktuelleY = pruefung.y;
                } else {

                    //Ecke bereits besucht? dann andere Richtung
                    if (listContains(ecken, new Points(aktuelleX, aktuelleY, richtung))) if (deadend != "") {
                        richtung = aendereRichtung(deadend, true);
                        deadend = "";
                    } else {
                        richtung = aendereRichtung(richtung, true);
                        goingBack = false;
                    }
                        //Neue Ecke erreicht -> zum aktuellen Pfad aufnehmen
                    else {
                        //aktuellerPfad.add(new Points(aktuelleX, aktuelleY, true));
                        //aktuellerPfad.add(new Points(aktuelleX, aktuelleY, true));
                        ecken.add(new Points(aktuelleX, aktuelleY, richtung));
                        richtung = aendereRichtung(richtung, false);
                        Log("Neue R " + richtung);
                    }
                }
            } else {
                //Strasse geht noch weiter -> gehe erstmal zu Ende
                //Punkt zu Pfad hinzufuegen
                aktuellerPfad.add(new Points(aktuelleX, aktuelleY, richtung));
                Log("Adding " + aktuellerPfad.get(aktuellerPfad.size() - 1));

                //weitergehen
                aktuelleX = xGeheWeiterInRichtung(richtung, aktuelleX);
                aktuelleY = yGeheWeiterInRichtung(richtung, aktuelleY);

                if (maxSteps != Integer.MAX_VALUE) {
                    maxSteps--;
                }
            }

            //Ringschluss gefunden
            if (optionen.mapTileNum[aktuelleX][aktuelleY] == 45) {
                if (zielCounter) {
                    //Ziel wird auch noch als Checkpunkt gesetzt
                    checkpunktMaximum = checkpunkte.size() + 1;
                    break;
                } else {
                    zielCounter = true;
                }
            }
            if (maxSteps == 0) {
                break;
            }
        }
    }

    /**
     * Setzt einen zufaelligen Nachbar-Punkt neben allen Checkpunkten.
     * Ist dabei eine Tile nicht akzeptiert, so wird die andere Nachbars-Tile verwendet.
     *
     * @param punkt Aktueller Punkt, der einen Nachbar benoetigt.
     * @return Zufaelliger Nachbar.
     */
    Points zufaelligerNachbar(Points punkt) {
        Points nachbar = new Points(punkt.x, punkt.y, punkt.richtung);
        Random rand = new Random();

        int diff = rand.nextInt(2);
        if (diff == 0) {
            diff = -1;
        }

        if (punkt.richtung == "up" || punkt.richtung == "down") {
            nachbar.x += diff;
            if (!checkStrasse(nachbar.x, nachbar.y)) {
                nachbar.x = punkt.x + (-1 * diff);
                if (!checkStrasse(nachbar.x, nachbar.y)) {
                    return punkt;
                }
            }

        } else {
            nachbar.y += diff;
            if (!checkStrasse(nachbar.x, nachbar.y)) nachbar.y = punkt.y + (-1 * diff);
            if (!checkStrasse(nachbar.x, nachbar.y)) {
                return punkt;
            }
        }
        return nachbar;
    }

    /**
     * Teilt den aktuellen Pfad in checkpunktAnzahl-Teile auf und setzt an
     * diesen Endpunkten die Checkpunkte
     */
    Map<Integer, List<TileKoordinate>> setzeCheckpunkte() {
        int steps = aktuellerPfad.size() / checkpunktAnzahl;

        Map<Integer, List<TileKoordinate>> checkPoints = new HashMap<>();
        for (int i = steps; i <= aktuellerPfad.size(); i += steps) {
            if(i >= aktuellerPfad.size()) {
                i = aktuellerPfad.size() - 1;
            }
            Points check = aktuellerPfad.get(i);
            Points nachbar = zufaelligerNachbar(check);

            List<TileKoordinate> checkpunktColl = new ArrayList<>();
            checkpunktColl.add(new TileKoordinate(check.x, check.y));
            checkpunktColl.add(new TileKoordinate(nachbar.x, nachbar.y));


            Log("Check: " + check.x + " " + check.y + " " + check.richtung);
            checkPoints.put(i / steps, checkpunktColl);
        }

        return checkPoints;
    }

    /**
     * Hauptprozedur, um Checkpunkte zu ermitteln
     *
     * @param checkpunktAnzahl Anzahl an Checkpunkten, die gesetzt werden sollen
     * @return Checkpunkte als ArrayList
     */
    public Map<Integer, List<TileKoordinate>> ermittleCheckpunkte(int checkpunktAnzahl) {
        TileKoordinate current = manager.getTileKoordinate(manager.posX, manager.posY);
        ermittlePfad(current.getTileX(), current.getTileY(), checkpunktAnzahl, Integer.MAX_VALUE, manager.direction);

        return setzeCheckpunkte();
    }

    /**
     * Prueft, ob List die TileKoordinate p enthaelt.
     *
     * @param list Zu pruefende Liste.
     * @param p    Zu pruefende TileKoordinate.
     * @return True: Liste enthaelt TileKoordinate, False: andernfalls.
     */
    boolean listContains(ArrayList<TileKoordinate> list, TileKoordinate p) {
        for (TileKoordinate i : list) {
            if (i.getTileX() == p.getTileX() && i.getTileY() == p.getTileY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt die ermittelten Checkpunkte samt Spielfeld aus.
     *
     * @param checkpunkte Die ermittelten Checkpunkte.
     */
    public void printCheckpunktMap(Map<Integer, List<TileKoordinate>> checkpunkte) {
        String output = "";
        ArrayList<TileKoordinate> totaleCheckpunkte = new ArrayList<>();
        for (Map.Entry<Integer, List<TileKoordinate>> entry : checkpunkte.entrySet()) {
            for (TileKoordinate tilek : entry.getValue()) {
                totaleCheckpunkte.add(new TileKoordinate(tilek.getTileX(), tilek.getTileY()));
            }
        }

        for (int i = 0; i < optionen.maxBildschirmZeilen; i++) {
            for (int j = 0; j < optionen.maxBildschirmSpalten; j++) {
                if (listContains(totaleCheckpunkte, new TileKoordinate(j, i))) {
                    output += "  C";
                } else {
                    if (optionen.mapTileNum[j][i] < 10) {
                        output += "  " + optionen.mapTileNum[j][i];
                    } else {
                        output += " " + optionen.mapTileNum[j][i];
                    }
                }
            }
            output += "\n";
        }
        Log(output);
    }

    /**
     * Ermittelt den naechsten Checkpunkt und fuegt diesen zu den Checkpunkten hinzu.
     * @param maxSteps Anzahl maximaler Tiles, die betrachtet werden sollen.
     */
    public void rechneWeiter(int maxSteps) {
        //Von aktueller Position starten
        //ErmittlePfad ausfueren
        TileKoordinate currentTile;
        String richtung = "";
        if (checkpunkte.size() == 0) {
            currentTile = manager.getTileKoordinate(manager.posX, manager.posY);
            richtung = manager.direction;
        } else {
            currentTile = checkpunkte.get(checkpunkte.size()).get(0);
            richtung = currentTile.richtung;
        }
        ermittlePfad(currentTile.getTileX(), currentTile.getTileY(), 1, maxSteps, richtung);

        //Endpunkt zu Checkpunkte hinzufuegen
        Points neuerCheckpunkt = aktuellerPfad.get(aktuellerPfad.size() - 1);
        Points neuerNachbar = zufaelligerNachbar(neuerCheckpunkt);
        ArrayList<TileKoordinate> neu = new ArrayList<>();
        neu.add(new TileKoordinate(neuerCheckpunkt.x, neuerCheckpunkt.y, neuerCheckpunkt.richtung));
        neu.add(new TileKoordinate(neuerNachbar.x, neuerNachbar.y, neuerNachbar.richtung));

        checkpunkte.put(checkpunkte.size() + 1, neu);
    }



    /**
     * Gibt den naechsten Checkpunkt zurueck.
     * Falls noch nicht das komplette Spielfeld betrachtet wurde, wird der naechste Checkpunkt ermittelt.
     * Ansonsten, wir der naechste Checkpunkt anhand der ID zurueckgegeben.
     * @param aktuelleID Die Anzahl an Checkpunkten, die bereits passiert wurden.
     * @param maxSteps Anzahl maximaler Tiles, die betrachtet werden sollen.
     * @return Den naechsten Checkpunkt inkl. Nachbar.
     */
    public List<TileKoordinate> nextCheckpunkt(int aktuelleID, int maxSteps) {
        //Es ist schon eine Rundreise bekannt -> nur naechsten Checkpkt ausgeben
        if (checkpunktMaximum != 0) {
            //Spieler ist schon ueber letzten Checkpunkt -> Wieder von vorne anfangen
            Log("Ich bin schon einmal hier gewesen... Ab jetzt muss ich nichts mehr rechnen");
            return checkpunkte.get((aktuelleID % checkpunktMaximum) + 1);
        }

        //Zeitmessung starten
        manager.zeitMessungStart();;

        //Naechsten Abschnitt ermitteln
        rechneWeiter(maxSteps);

        //Zeitmessung enden
        manager.zeitMessungEnde();

        Log("Zeitmessung Iteration: " + manager.zeitMessungDurchschnitt());

        aktuellerPfad.remove(aktuellerPfad.size() - 1);
        return checkpunkte.get(aktuelleID + 1);
    }


}
