package kiansichtsschicht;
import anwendungsschicht.EventManager;
import kiansichtsschicht.TrivialeKI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TrivialeKITest {

    TrivialeKI createTrivial() {
        TrivialeKI trivialeKI = new TrivialeKI();
        trivialeKI.setVerbose(false);
        return trivialeKI;
    }

    @Test
    public void test_resetPressed_up() {
        TrivialeKI trivial = createTrivial();
        trivial.resetPressed();
        assertEquals(false, trivial.upPressed);
    }

    @Test
    public void test_resetPressed_down() {
        TrivialeKI trivial = createTrivial();
        trivial.resetPressed();
        assertEquals(false, trivial.downPressed);
    }

    @Test
    public void test_resetPressed_right() {
        TrivialeKI trivial = createTrivial();
        trivial.resetPressed();
        assertEquals(false, trivial.rightPressed);
    }

    @Test
    public void test_resetPressed_left() {
        TrivialeKI trivial = createTrivial();
        trivial.resetPressed();
        assertEquals(false, trivial.leftPressed);
    }

    @Test
    public void test_saveMapTile_different_Tile_Richtung() {
        TrivialeKI trivial = createTrivial();
        String richtung = "up";

        Object eventData[] = new Object[] { 0, richtung, 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.saveMapTile(richtung);
        assertEquals(richtung, trivial.saveDirection);
    }

    @Test
    public void test_saveMapTile_different_Tile_X() {
        TrivialeKI trivial = createTrivial();

        Object eventData[] = new Object[] { 0, "richtung", 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.saveMapTile("richtung");
        assertEquals(21, trivial.saveMapTileX);
    }

    @Test
    public void test_saveMapTile_different_Tile_Y() {
        TrivialeKI trivial = createTrivial();

        Object eventData[] = new Object[] { 0, "richtung", 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.saveMapTile("richtung");
        assertEquals(42, trivial.saveMapTileY);
    }

    @Test
    public void test_saveMapTile_same_Y() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 21;
        trivial.saveMapTileY = 42;
        trivial.saveDirection = "up";

        Object eventData[] = new Object[] { 0, "richtung", 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.saveMapTile("richtung");
        assertEquals(42, trivial.saveMapTileY);
    }

    @Test
    public void test_saveMapTile_same_X() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 21;
        trivial.saveMapTileY = 42;
        trivial.saveDirection = "up";

        Object eventData[] = new Object[] { 0, "richtung", 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.saveMapTile("richtung");
        assertEquals(21, trivial.saveMapTileX);
    }

    @Test
    public void test_saveMapTile_same_Richtung() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 21;
        trivial.saveMapTileY = 42;
        trivial.saveDirection = "up";

        Object eventData[] = new Object[] { 0, "richtung", 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.saveMapTile("richtung");
        assertEquals("up", trivial.saveDirection);
    }

    @Test
    public void test_pressRichtung_Left() {
        TrivialeKI trivial = createTrivial();

        trivial.pressRichtung("left");

        assertTrue(trivial.leftPressed);
    }

    @Test
    public void test_pressRichtung_Right() {
        TrivialeKI trivial = createTrivial();

        trivial.pressRichtung("right");

        assertTrue(trivial.rightPressed);
    }

    @Test
    public void test_pressRichtung_Up() {
        TrivialeKI trivial = createTrivial();

        trivial.pressRichtung("up");

        assertTrue(trivial.upPressed);
    }

    @Test
    public void test_pressRichtung_Down() {
        TrivialeKI trivial = createTrivial();

        trivial.pressRichtung("down");

        assertTrue(trivial.downPressed);
    }

    @Test
    public void test_checkStrasse_nicht_erlaubt() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {1, 1, 1, 1},
                {2, 2, 2, 2}
        };

        boolean actual = trivial.checkStrasse(0, 0);
        assertFalse(actual);
    }

    @Test
    public void test_checkStrasse_erlaubt_Strasse() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {11, 1, 1, 1},
                {2, 2, 2, 2}
        };

        boolean actual = trivial.checkStrasse(0, 0);
        assertTrue(actual);
    }

    @Test
    public void test_checkStrasse_erlaubt_Ziel() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {45, 1, 1, 1},
                {2, 2, 2, 2}
        };

        boolean actual = trivial.checkStrasse(0, 0);
        assertTrue(actual);
    }

    @Test
    public void test_checkStrasse_erlaubt_Startposition() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {9, 1, 1, 1},
                {2, 2, 2, 2}
        };

        boolean actual = trivial.checkStrasse(0, 0);
        assertTrue(actual);
    }

    @Test
    public void test_pruefeLaengsterWeg_Up() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {9, 1, 1, 1},
                {2, 11, 11, 2},
                {3, 11, 11, 3}
        };

        //11 unten links
        int actual = trivial.pruefeLaengsterWeg("up", 60, 100);

        assertEquals(4, actual);
    }

    @Test
    public void test_pruefeLaengsterWeg_Down() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {9, 1, 1, 1},
                {2, 11, 11, 2},
                {3, 11, 11, 3}
        };

        //11 oben links
        int actual = trivial.pruefeLaengsterWeg("down", 60, 50);

        assertEquals(3, actual);
    }

    @Test
    public void test_pruefeLaengsterWeg_Left() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {9, 1, 1, 1},
                {2, 11, 11, 2},
                {3, 11, 11, 3}
        };

        //11 oben links
        int actual = trivial.pruefeLaengsterWeg("left", 60, 50);

        assertEquals(2, actual);
    }


    @Test
    public void test_pruefeLaengsterWeg_Right() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {1, 1, 1, 1},
                {2, 11, 11, 2},
                {3, 3, 3, 3}
        };

        //11 oben links
        int actual = trivial.pruefeLaengsterWeg("right", 50, 50);

        assertEquals(1, actual);
    }

    @Test
    public void test_pruefeLaengsterWeg_nicht_erlaubte_Strasse() {
        TrivialeKI trivial = createTrivial();
        trivial.manager.optionen.mapTileNum = new int[][] {
                {1, 1, 1, 1},
                {2, 11, 11, 2},
                {3, 11, 11, 3}
        };

        //1 oben links
        int actual = trivial.pruefeLaengsterWeg("up", 0, 0);

        assertEquals(0, actual);
    }

    @Test
    public void test_vomWegAbgekommen_rechts_von_save() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 30;
        trivial.saveMapTileY = 42;
        trivial.saveDirection = "up";
        Object eventData[] = new Object[] { 0, "left", 1000, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.rightPressed);
    }

    @Test
    public void test_vomWegAbgekommen_links_von_save() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 15;
        trivial.saveMapTileY = 42;
        trivial.saveDirection = "up";
        Object eventData[] = new Object[] { 0, "left", 1300, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.leftPressed);
    }


    @Test
    public void test_vomWegAbgekommen_unter_save_left() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 0;
        trivial.saveMapTileY = 0;
        trivial.saveDirection = "left";
        Object eventData[] = new Object[] { 0, "left", 1300, 2000};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.upPressed);
    }

    @Test
    public void test_vomWegAbgekommen_ueber_save_right() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 1;
        trivial.saveMapTileY = 1;
        trivial.saveDirection = "right";
        Object eventData[] = new Object[] { 0, "left", 0, 0};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.downPressed);
    }

    @Test
    public void test_vomWegAbgekommen_neben_save_right() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 1;
        trivial.saveMapTileY = 1;
        trivial.saveDirection = "right";
        Object eventData[] = new Object[] { 0, "left", 0, 50};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.rightPressed);
    }

    @Test
    public void test_vomWegAbgekommen_neben_save_left() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 1;
        trivial.saveMapTileY = 1;
        trivial.saveDirection = "left";
        Object eventData[] = new Object[] { 0, "left", 100, 50};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.leftPressed);
    }


    @Test
    public void test_vomWegAbgekommen_ueber_save_up() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 1;
        trivial.saveMapTileY = 1;
        trivial.saveDirection = "up";
        Object eventData[] = new Object[] { 0, "left", 50, 0};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.downPressed);
    }

    @Test
    public void test_vomWegAbgekommen_unter_save_down() {
        TrivialeKI trivial = createTrivial();
        trivial.saveMapTileX = 1;
        trivial.saveMapTileY = 1;
        trivial.saveDirection = "up";
        Object eventData[] = new Object[] { 0, "left", 50, 100};
        trivial.manager.updateEvent("update_koordinate", eventData);

        trivial.vomWegAbgekommen();

        assertTrue(trivial.upPressed);
    }
    
    @Test
    public void test_ermittleRichtung_Direction_ist_null() {
        TrivialeKI trivial = createTrivial();
        trivial.ermittleRichtung();

        assertTrue(!trivial.upPressed && !trivial.downPressed
                    && !trivial.leftPressed && !trivial.rightPressed);
    }

    @Test
    public void test_schickeRichtung() {
        TrivialeKI trivial = createTrivial();
        trivial.upPressed = true;

        trivial.schickeRichtung();
        // Wie kann man events testen?
    }

    @Test
    public void test_gegenRichtung_up() {
        TrivialeKI trivial = createTrivial();
        String actual = trivial.gegenRichtung("up");
        assertEquals("down", actual);
    }

    @Test
    public void test_gegenRichtung_down() {
        TrivialeKI trivial = createTrivial();
        String actual = trivial.gegenRichtung("down");
        assertEquals("up", actual);
    }

    @Test
    public void test_gegenRichtung_left() {
        TrivialeKI trivial = createTrivial();
        String actual = trivial.gegenRichtung("left");
        assertEquals("right", actual);
    }

    @Test
    public void test_gegenRichtung_right() {
        TrivialeKI trivial = createTrivial();
        String actual = trivial.gegenRichtung("right");
        assertEquals("left", actual);
    }


}
