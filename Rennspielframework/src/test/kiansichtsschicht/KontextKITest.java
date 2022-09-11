package kiansichtsschicht;

import static org.junit.Assert.*;

import kiansichtsschicht.AKI;
import kiansichtsschicht.KontextKI;
import kiansichtsschicht.TrivialeKI;
import org.junit.Test;

public class KontextKITest {
    @Test
    public void test_ermittleKI_trivial() {
        String args[] = new String[]{"client", "8000", "localhost", "KI", "trivial"};
        AKI AKI = KontextKI.ermittleKI(args[4]);

        assertTrue(AKI instanceof TrivialeKI);
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
