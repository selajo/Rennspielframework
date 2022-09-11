package kiansichtsschicht;
import static org.junit.Assert.*;

import anwendungsschicht.TileKoordinate;
import kiansichtsschicht.KISpielObjekteManager;
import org.junit.Test;


public class KISpielObjekteManagerTest {
    @Test
    public void test_getTileKoordinate_return_Wert_X() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        TileKoordinate actual = manager.getTileKoordinate(1000, 300);

        TileKoordinate expected = new TileKoordinate(21, 1);
        assertEquals(expected.getTileX(), actual.getTileX());
    }

    @Test
    public void test_getTileKoordinate_return_Wert_Y() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        TileKoordinate actual = manager.getTileKoordinate(1000, 300);

        TileKoordinate expected = new TileKoordinate(21, 6);
        assertEquals(expected.getTileY(), actual.getTileY());
    }

    @Test
    public void test_getTileTypVonPosition_return_Wert() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        manager.optionen.mapTileNum = new int[][] {
                {1, 1, 1, 1},
                {2, 2, 2, 2},
                {3, 3, 3, 3}
        };
        int actual = manager.getTileTypVonPosition(100, 100);

        assertEquals(3, actual);
    }

    @Test
    public void test_getTileTypVonPosition_out_of_bounds_negativ() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        manager.optionen.mapTileNum = new int[][] {
                {1, 1, 1, 1},
                {2, 2, 2, 2},
                {3, 3, 3, 3}
        };
        int actual = manager.getTileTypVonPosition(-100, -100);

        assertEquals(-1, actual);
    }

    @Test
    public void test_getTileTypVonPosition_out_of_bounds_positiv() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        manager.optionen.mapTileNum = new int[][] {
                {1, 1, 1, 1},
                {2, 2, 2, 2},
                {3, 3, 3, 3}
        };
        int actual = manager.getTileTypVonPosition(2000, 2000);

        assertEquals(-1, actual);
    }

    @Test
    public void test_updateEvent_direction() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        String richtung = "right";

        Object eventData[] = new Object[] { 2, richtung, 1000, 2000};
        manager.updateEvent("update_koordinate", eventData);

        assertEquals(richtung, manager.getDirection());
    }

    @Test
    public void test_updateEvent_posX() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        int posX = 1000;

        Object eventData[] = new Object[] { 2, "right", posX, 2000};
        manager.updateEvent("update_koordinate", eventData);

        assertEquals(posX, manager.getPosX());
    }

    @Test
    public void test_updateEvent_posY() {
        KISpielObjekteManager manager = new KISpielObjekteManager();
        int posY = 2000;

        Object eventData[] = new Object[] { 2, "right", 1000, posY};
        manager.updateEvent("update_koordinate", eventData);

        assertEquals(posY, manager.getPosY());
    }
}
