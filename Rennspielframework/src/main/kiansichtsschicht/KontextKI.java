package kiansichtsschicht;

/**
 * Erzeuge richtigen KI-Typ anhand arg. Bei unbekannter Eingabe wird Exception geworfen.
 */
public class KontextKI {
    /**
     * Erzeuge richtigen KI-Typ anhand arg. Bei unbekannter Eingabe wird Exception geworfen.
     * @param arg Zu pruefnde Eingabe
     * @return Kind-Instanz der IKI
     */
    public static AKI ermittleKI(String arg) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (arg.equals("trivial")) {
            return new TrivialeKI();
        } else if (arg.equals("astern")) {
            return new ASternKI();
        } else if (arg.equals("dijkstra")) {
            return new DijkstraKI();
        }
        //Unbekannte Eingabe
        else {
            throw new IllegalArgumentException("Der Typ der zu verwendenden KI muss spezifiziert sein");
        }
    }
}
