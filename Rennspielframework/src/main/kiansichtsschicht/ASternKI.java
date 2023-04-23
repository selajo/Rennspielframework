package kiansichtsschicht;

import java.util.*;

/**
 * A* KI-CLient der kürzeste Wege berechnet.
 */

public class ASternKI extends AKI {

    /**
     * SpielKnoten die noch zu bearbeiten sind
     */
    ArrayList<SpielKnoten> offeneKnoten;

    /**
     * Spielknoten die bereits bearbeitet wurden.
     */
    ArrayList<SpielKnoten> geschlosseneKnoten;


    /**
     * Erzeugt ASternKI
     */
    public ASternKI(){
        logger.info("A-Stern-KI-Client startet.");
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
     * A*-Logik die den kürzesten Pfad berechnet
     */
    public void aStern() {

        if (!graphAlgorithmusStartUp()){
            return;
        }

        long startTime = System.nanoTime();

        Strecke = new ArrayList<>();

        offeneKnoten = new ArrayList<>();

        geschlosseneKnoten = new ArrayList<>();

        offeneKnoten.add(StartKnoten);

        while (!offeneKnoten.isEmpty()) {

            SpielKnoten current = getNiedrigstesF();
            current.berechneNachbarn(spielGraph.getAsphaltKnoten());

            if (current.equals(ZielKnoten)) {
                retraceStrecke(current);
                break;
            }

            offeneKnoten.remove(current);
            geschlosseneKnoten.add(current);

            for (SpielKnoten sk : current.getNachbarKnoten()) {

                if (geschlosseneKnoten.contains(sk) || !sk.isValide()) {
                    continue;
                }

                double tempScore = current.getKosten() + current.distanzZu(sk);

                if (offeneKnoten.contains(sk)) {
                    if (tempScore < sk.getKosten()) {
                        sk.setKosten(tempScore);
                        sk.setParent(current);
                    }
                } else {
                    sk.setKosten(tempScore);
                    offeneKnoten.add(sk);
                    sk.setParent(current);
                }

                sk.setHeuristik(sk.heuristik(ZielKnoten));
                sk.setFunktion(sk.getKosten() + sk.getHeuristik());
            }
        }

        long timeNeededToGetPath = System.nanoTime() - startTime;

        logger.info(timeNeededToGetPath);

        graphAlgorithmusRichtungsentscheidung(); // Richtung wird ermittelt

        //graphAlgorithmusBremseVorKurve(); // optional; SpielKnotenspeicher unten gehört dazu

        // berechnete Richtung weitergeben
        pressRichtung(richtung);

        logger.info("Richtung: " + richtung);

        // aktuelle Position wieder speichern
        if(checkStrasse(manager.posX, manager.posY)) {
            saveMapTile(richtung);
        }

        SpielKnotenSpeicher = nächsterSpielKnoten;
        SpielKnotenSpeicher.setParent(aktuellerSpielKnoten);
    }


    /**
     * Niedrigste Kosten werden ermittelt.
     */
    public SpielKnoten getNiedrigstesF() {
        SpielKnoten niedrigst = offeneKnoten.get(0);
        for (SpielKnoten sk : offeneKnoten) {
            if (sk.getFunktion()< niedrigst.getFunktion()) {
                niedrigst = sk;
            }
        }
        return niedrigst;
    }

    /**
     * kürzester Pfad wird ermittelt durch rückwärtsgehen zum Startknoten
     */
    private void retraceStrecke(SpielKnoten current) {
        SpielKnoten temp = current;
        this.Strecke.add(current);

        while (temp.getParent() != null) {
            this.Strecke.add(temp.getParent());
            temp = temp.getParent();
        }
    }

    /**
     * Ermittelt Richtung und schicht diese als Event
     */
    @Override
    public void update(){
        aStern();
        schickeRichtung();
    }

}
