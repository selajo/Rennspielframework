package kiansichtsschicht;

import static org.junit.Assert.*;

import anwendungsschicht.Spieloptionen;
import kiansichtsschicht.AKI;
import kiansichtsschicht.KontextKI;
import kiansichtsschicht.TrivialeKI;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class KontextKITest {
    @BeforeClass
    public static void setUp() {
        Spieloptionen optionen = Spieloptionen.getInstance();
        optionen.setSpieloptionen("/Res/Config/ConfigWorld00.json");
    }

    @Test
    public void test_ermittleKI_trivial() {
        String args[] = new String[]{"client", "8000", "localhost", "KI", "trivial"};
        AKI AKI = KontextKI.ermittleKI(args[4]);

        assertTrue(AKI instanceof TrivialeKI);
    }

    @Ignore
    @Test
    public void test_ermittleKI_astern() {
        String args[] = new String[]{"client", "8000", "localhost", "KI", "astern"};
        AKI AKI = KontextKI.ermittleKI(args[4]);

        assertTrue(AKI instanceof ASternKI);
    }

    @Ignore
    @Test
    public void test_ermittleKI_dijkstra() {
        String args[] = new String[]{"client", "8000", "localhost", "KI", "dijkstra"};
        AKI AKI = KontextKI.ermittleKI(args[4]);

        assertTrue(AKI instanceof DijkstraKI);
    }

    @Test
    public void test_unbekannte_ki() {
        String args[] = new String[]{"client", "8000", "localhost", "KI", "unbekannt"};
        try {
            AKI AKI = KontextKI.ermittleKI(args[4]);
        } catch (Exception e) {
            assertEquals("Der Typ der zu verwendenden KI muss spezifiziert sein", e.getMessage());
        }
    }
}
