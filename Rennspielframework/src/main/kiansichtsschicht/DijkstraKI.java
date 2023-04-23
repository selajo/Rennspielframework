package kiansichtsschicht;

import java.util.*;

/**
 * Dijksra KI-CLient der kürzeste Wege berechnet.
 */

public class DijkstraKI extends AKI {

    /**
     * Prioritätenliste für abgearbeitete Spielknoten
     */
    ArrayList<SpielKnoten> prioListe;

    /**
     * Erzeugt ASternKI
     */
    public DijkstraKI() {
        logger.info("Dijkstra-KI-Client startet.");
        manager = new KISpielObjekteManager();
        spielGraph = new SpielGraph();
        checkpointManager = new CheckpointManager(manager);
        resetPressed();
        saveMapTileY = saveMapTileX = 0;
        saveDirection = "";
        zurueckAufStrasse = false;
        kollidiert = false;
    }

    /**
     * Dijkstra-Logik die den kürzesten Pfad berechnet
     */
    public void dijsktra() {

        if (!graphAlgorithmusStartUp()) {
            return;
        }

        long startTime = System.nanoTime();

        if (rundeBeendet) {
            rundeBeendet = false;
        }

        prioListe = new ArrayList<>();

        Strecke = new ArrayList<>();

        for (SpielKnoten sk : spielGraph.getAlleSpielKnotenAlsArrayList()) {
            sk.setDistanz(Double.MAX_VALUE); // Distanz unendlich setzen
            sk.gesehen = false; // Knoten noch nicht gesehen
            sk.setParent(null); // Parent noch nicht ermittelt
        }

        StartKnoten.setDistanz(0);

        StartKnoten.startPunkt = true;

        StartKnoten.gesehen = false;

        prioListe.add(StartKnoten);

        while (!prioListe.isEmpty()) {

            if ((prioListe.get(0)) != ZielKnoten) {
                berechneNächstenKnoten(prioListe.get(0));
            }

            // der kürzeste Pfad wird durch rückwärts Gehen ermittelt
            Strecke = getErgebnisPfad();

            if (Strecke != null) {

                long timeNeededToGetPath = System.nanoTime() - startTime;

                logger.info(timeNeededToGetPath);

                graphAlgorithmusRichtungsentscheidung(); // Richtung wird ermittelt

                graphAlgorithmusBremseVorKurve(); // optional; SpielKnotenspeicher unten gehört dazu

                pressRichtung(richtung);

                logger.info("Richtung: " + richtung);

                // aktuelle Position wieder speichern
                if (checkStrasse(manager.posX, manager.posY)) {
                    saveMapTile(richtung);
                }

                // Merke den aktuellen Knoten und die aktuelle Richtung für den Bremsvorgang
                SpielKnotenSpeicher = nächsterSpielKnoten;
                SpielKnotenSpeicher.setParent(aktuellerSpielKnoten);

                return;
            }
        }
    }

    /**
     * nächster Spielknoten auf kürzestem Pfad wird ermittelt
     */
    private void berechneNächstenKnoten(SpielKnoten aktuellerKnoten) {

        prioListe.remove(aktuellerKnoten);
        // Falls es sich um den Zielpunkt handelt, wird abgebrochen
        if (aktuellerKnoten.equals(ZielKnoten)) {
            Collections.sort(prioListe);
            return;
        }

        if (aktuellerKnoten.gesehen) {
            return;
        }   // falls schon bearbeitet
        aktuellerKnoten.gesehen = true;

        // Die Nachbarn nach Entfernung sortieren.
        ArrayList<SpielKnoten> nachbarKnoten = new ArrayList<>();
        // Nachbarn berechnen/setzen
        aktuellerKnoten.berechneNachbarn(spielGraph.getAsphaltKnoten());
        // Nachbarn einfügen/holen
        nachbarKnoten.addAll(aktuellerKnoten.getNachbarKnoten());
        // Vorgänger entfernen
        nachbarKnoten.remove(aktuellerKnoten.getParent());


        // Jeden direkten Nachbar durchlaufen.
        for (SpielKnoten aktuellerNachbar : nachbarKnoten) {
            // Für jeden Nachbar den Vorgänger setzen, falls eine kürzere Route gefunden wurde.
            double distanz = aktuellerKnoten.distanzZu(aktuellerNachbar);

            if (aktuellerNachbar.berechneEntfernungRekursiv() > aktuellerKnoten.berechneEntfernungRekursiv() + distanz
                    || aktuellerNachbar.berechneEntfernungRekursiv() == -1) {
                aktuellerNachbar.setParent(aktuellerKnoten);
            }

            if (!prioListe.contains(aktuellerNachbar)) {
                prioListe.add(aktuellerNachbar);
            }

            if (aktuellerNachbar == ZielKnoten) {
                return;
            }
        }

    }

    /**
     * kürzester Pfad wird durch rückwärts gehen ermittelt
     */
    public ArrayList<SpielKnoten> getErgebnisPfad() {

        ArrayList<SpielKnoten> pfad = new ArrayList<>();
        for (SpielKnoten sk = ZielKnoten; sk != null; sk = sk.getParent()) {
            pfad.add(sk);
        }
        if (pfad.size() >= 2) {
            return pfad;
        } else {
            return null;
        }
    }

    /**
     * Ermittelt Richtung und schicht diese als Event
     */
    @Override
    public void update() {
        dijsktra();
        schickeRichtung();
    }


}
