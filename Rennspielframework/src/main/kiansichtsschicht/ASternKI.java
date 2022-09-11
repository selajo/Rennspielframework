package kiansichtsschicht;

import java.util.*;

/**
 * A* KI-CLient der kürzeste Wege berechnet.
 */

public class ASternKI extends AKI {

    ArrayList<SpielKnoten> offeneKnoten;
    ArrayList<SpielKnoten> geschlosseneKnoten;


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


    public void aStern() {

        if (!graphAlgorithmusStartUp()){
            return;
        }

        if (rundeBeendet){
            System.out.println("rundeBeendet");
        }

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


        graphAlgorithmusRichtungsentscheidung(); // Richtung wird ermittelt

        //graphAlgorithmusBremseVorKurve(); // optional; SpielKnotenspeicher unten gehört dazu

        // berechnete Richtung weitergeben
        pressRichtung(richtung);



        logger.info("Richtung: " + richtung);

        // aktuelle Position wieder speichern
        if(checkStrasse(manager.posX, manager.posY)) {
            saveMapTile(richtung);
        }
        // Merke den aktuellen Knoten und die aktuelle Richtung für den Bremsvorgang
        SpielKnotenSpeicher = nächsterSpielKnoten;
        SpielKnotenSpeicher.setParent(aktuellerSpielKnoten);
    }



    public SpielKnoten getNiedrigstesF() {
        /**
         * A* CLient-relevante Funktion.
         */
        SpielKnoten niedrigst = offeneKnoten.get(0);
        for (SpielKnoten sk : offeneKnoten) {
            if (sk.getFunktion()< niedrigst.getFunktion()) {
                niedrigst = sk;
            }
        }
        return niedrigst;
    }

    private void retraceStrecke(SpielKnoten current) {
        /**
         * A* Client-relevante Funktion.
         */
        SpielKnoten temp = current;
        this.Strecke.add(current);

        while (temp.getParent() != null) {
            this.Strecke.add(temp.getParent());
            temp = temp.getParent();
        }
    }

    @Override
    public void update(){
        aStern();
        schickeRichtung();
    }

}
