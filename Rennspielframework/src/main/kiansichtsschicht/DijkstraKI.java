package kiansichtsschicht;

import java.util.*;

/**
 * Dijksra KI-CLient der kürzeste Wege berechnet.
 */

public class DijkstraKI extends AKI {

    ArrayList<SpielKnoten> prioListe;


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


    public void dijsktra() {

        if (!graphAlgorithmusStartUp()){
            return;
        }

        if (rundeBeendet){
            System.out.println("rundeBeendet");
        }

        prioListe = new ArrayList<>();

        Strecke = new ArrayList<>();

        for (SpielKnoten sk : spielGraph.getAlleSpielKnotenAlsArrayList()){
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
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
            //System.out.println(Strecke);
        if (Strecke != null) {

            graphAlgorithmusRichtungsentscheidung(); // Richtung wird ermittelt

            graphAlgorithmusBremseVorKurve(); // optional; SpielKnotenspeicher unten gehört dazu

            pressRichtung(richtung);

            logger.info("Richtung: " + richtung);

            // aktuelle Position wieder speichern
            if(checkStrasse(manager.posX, manager.posY)) {
                saveMapTile(richtung);
            }

            // Merke den aktuellen Knoten und die aktuelle Richtung für den Bremsvorgang
            SpielKnotenSpeicher = nächsterSpielKnoten;
            SpielKnotenSpeicher.setParent(aktuellerSpielKnoten);

            return;
            }
        }
    }



    private void berechneNächstenKnoten(SpielKnoten aktuellerKnoten) {
        /**
         * Dijkstra Client-relevante Funktion.
         */
        prioListe.remove(aktuellerKnoten);
        // Falls es sich um den Zielpunkt handelt, wird abgebrochen
        if (aktuellerKnoten.equals(ZielKnoten)) {
            Collections.sort(prioListe);
            return;
        }

        if (aktuellerKnoten.gesehen){ return;}   // falls schon bearbeitet
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
                //System.out.println("aktuellerNachbar.setParent(aktuellerKnoten): " + aktuellerNachbar.getParent());
            }
            if (!prioListe.contains(aktuellerNachbar)) {
                prioListe.add(aktuellerNachbar);
            }
        }

    }

    public ArrayList<SpielKnoten> getErgebnisPfad(){
        /**
         * Dijkstra Client-relevante Funktion.
         */
        ArrayList<SpielKnoten> pfad = new ArrayList<>();
        for (SpielKnoten sk = ZielKnoten; sk!=null; sk=sk.getParent()){
            //System.out.println("sk.getParent(): " + sk.getParent());
            pfad.add(sk);
        }
        //System.out.println(pfad.size());
        if (pfad.size() >= 2 ) {
            return pfad;
        }
        else {
            return null;
        }
    }

    @Override
    public void update() {
        dijsktra();
        schickeRichtung();
    }













}
