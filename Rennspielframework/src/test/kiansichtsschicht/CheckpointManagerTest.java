package kiansichtsschicht;


import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CheckpointManagerTest {
    @Test
    public void test_gegenRichtung_up() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.gegenRichtung("up");
        assertEquals("down", actual);
    }

    @Test
    public void test_gegenRichtung_down() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.gegenRichtung("down");
        assertEquals("up", actual);
    }

    @Test
    public void test_gegenRichtung_left() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.gegenRichtung("left");
        assertEquals("right", actual);
    }

    @Test
    public void test_gegenRichtung_right() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.gegenRichtung("right");
        assertEquals("left", actual);
    }

    @Test
    public void test_andereRichtung_ersterVersuch_Up() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.aendereRichtung("up", false);
        assertEquals("right", actual);
    }

    @Test
    public void test_andereRichtung_ersterVersuch_Left() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.aendereRichtung("left", false);
        assertEquals("up", actual);
    }

    @Test
    public void test_andereRichtung_zweiterVersuch_Down() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.aendereRichtung("up", true);
        assertEquals("down", actual);
    }

    @Test
    public void test_andereRichtung_zweiterVersuch_Right() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        String actual = checkpointManager.aendereRichtung("right", true);
        assertEquals("left", actual);
    }

    @Test
    public void test_xGeheWeiterInRichtung_Rechts() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        int actual = checkpointManager.xGeheWeiterInRichtung("right", 5);
        assertEquals(6, actual);
    }

    @Test
    public void test_xGeheWeiterInRichtung_Links() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        int actual = checkpointManager.xGeheWeiterInRichtung("left", 5);
        assertEquals(4, actual);
    }

    @Test
    public void test_xGeheWeiterInRichtung_Oben() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        int actual = checkpointManager.xGeheWeiterInRichtung("up", 5);
        assertEquals(5, actual);
    }

    @Test
    public void test_yGeheWeiterInRichtung_Oben() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        int actual = checkpointManager.yGeheWeiterInRichtung("up", 3);
        assertEquals(2, actual);
    }

    @Test
    public void test_yGeheWeiterInRichtung_Unten() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        int actual = checkpointManager.yGeheWeiterInRichtung("down", 3);
        assertEquals(4, actual);
    }

    @Test
    public void test_yGeheWeiterInRichtung_Links() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        int actual = checkpointManager.yGeheWeiterInRichtung("left", 3);
        assertEquals(3, actual);
    }

    @Test
    public void test_listConstains_Points_True() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        ArrayList<CheckpointManager.Points> list = new ArrayList<>();
        list.add(new CheckpointManager.Points(1, 1, "left"));
        boolean actual = checkpointManager.listContains(list, new CheckpointManager.Points(1, 1, "right"));
        assertTrue(actual);
    }

    @Test
    public void test_listConstains_Points_False() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        ArrayList<CheckpointManager.Points> list = new ArrayList<>();
        list.add(new CheckpointManager.Points(1, 1, "left"));
        boolean actual = checkpointManager.listContains(list, new CheckpointManager.Points(2, 1, "right"));
        assertFalse(actual);
    }

    @Test
    public void test_listConstains_TileKoordinate_True() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        ArrayList<TileKoordinate> list = new ArrayList<>();
        list.add(new TileKoordinate(2, 1));
        boolean actual = checkpointManager.listContains(list, new TileKoordinate(2, 1));
        assertTrue(actual);
    }

    @Test
    public void test_listConstains_TileKoordinate_False() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        ArrayList<TileKoordinate> list = new ArrayList<>();
        list.add(new TileKoordinate(2, 1));
        boolean actual = checkpointManager.listContains(list, new TileKoordinate(2, 3));
        assertFalse(actual);
    }

    @Test
    public void test_Points_isInArea_empty() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        CheckpointManager.Points p = new CheckpointManager.Points(1, 2, "");
        List<CheckpointManager.Points> list = new ArrayList<>();

        Assert.assertFalse(p.isInArea(list, 1));
    }

    @Test
    public void test_Points_isInArea_false() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        CheckpointManager.Points p = new CheckpointManager.Points(1, 2, "");
        List<CheckpointManager.Points> list = new ArrayList<>();
        list.add(new CheckpointManager.Points(3, 5, ""));

        Assert.assertFalse(p.isInArea(list, 1));
    }

    @Test
    public void test_Points_isInArea_true() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        CheckpointManager.Points p = new CheckpointManager.Points(1, 2, "");
        List<CheckpointManager.Points> list = new ArrayList<>();
        list.add(new CheckpointManager.Points(3, 5, ""));
        list.add(new CheckpointManager.Points(2, 2, ""));

        Assert.assertTrue(p.isInArea(list, 1));
    }

    @Test
    public void test_zurueckZuLetzterEcke() {
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        checkpointManager.ecken = new ArrayList<>();
        checkpointManager.ecken.add(new CheckpointManager.Points(1, 1, "up"));
        checkpointManager.aktuellerPfad = new ArrayList<>();
        checkpointManager.aktuellerPfad.add(new CheckpointManager.Points(1, 1, "up"));
        checkpointManager.aktuellerPfad.add(new CheckpointManager.Points(1, 2, "up"));
        checkpointManager.aktuellerPfad.add(new CheckpointManager.Points(1, 2, "up"));
        int actual = checkpointManager.zurueckZurLetztenEcke();

        assertEquals(3, actual);
    }

    @Test
    public void test_setzeCheckpunkte() {
        Spieloptionen spieloptionen = new Spieloptionen();
        spieloptionen.mapTileNum = new int[][] {
                {10, 10, 10, 10, 10, 10},
                {11, 11, 11, 11, 11, 11},
                {11, 11, 11, 11, 11, 11},
                {10, 10, 10, 10, 10, 10}
        };
        CheckpointManager checkpointManager = new CheckpointManager(new KISpielObjekteManager());
        checkpointManager.checkpunktAnzahl = 3;
        checkpointManager.aktuellerPfad = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            checkpointManager.aktuellerPfad.add(new CheckpointManager.Points(i, 1, "right"));
        }

        Map<Integer, List<TileKoordinate>> checkpunkte = checkpointManager.setzeCheckpunkte();
        assertEquals(5, checkpunkte.get(checkpunkte.size()).get(0).getTileX());
        assertEquals(1, checkpunkte.get(checkpunkte.size()).get(0).getTileY());
    }
}
