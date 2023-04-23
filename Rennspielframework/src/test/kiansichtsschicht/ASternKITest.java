package kiansichtsschicht;


import anwendungsschicht.Spieloptionen;
import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.junit.Assert.*;


public class ASternKITest {
    static SpielGraph spielGraph;

    static SpielKnoten spielKnoten;


    @BeforeClass
    public static void setUp() {
        Spieloptionen optionen = Spieloptionen.getInstance();
        optionen.setSpieloptionen("/Res/Config/ConfigWorld00.json");
        spielGraph = new SpielGraph();
        spielKnoten = new SpielKnoten(1,2);
    }

    @Test
    public void test_resetPressed_up() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.upPressed);
    }

    @Test
    public void test_resetPressed_down() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.downPressed);
    }

    @Test
    public void test_resetPressed_right() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.rightPressed);
    }

    @Test
    public void test_resetPressed_left() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.leftPressed);
    }

    @Test
    public void test_resetPressed_up_right() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.upPressed && aStern.rightPressed);
    }

    @Test
    public void test_resetPressed_up_left() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.upPressed && aStern.leftPressed);
    }

    @Test
    public void test_resetPressed_down_left() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.downPressed && aStern.leftPressed);
    }

    @Test
    public void test_resetPressed_down_right() {
        ASternKI aStern = new ASternKI();
        aStern.resetPressed();
        assertEquals(false, aStern.downPressed && aStern.rightPressed);
    }
    @Test
    public void test_pressRichtung_left() {
        ASternKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("left");
        assertTrue(aSternKI.leftPressed);
    }

    @Test
    public void test_pressRichtung_right() {
        ASternKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("right");
        assertTrue(aSternKI.rightPressed);
    }

    @Test
    public void test_pressRichtung_down() {
        ASternKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("down");
        assertTrue(aSternKI.downPressed);
    }

    @Test
    public void test_pressRichtung_up() {
        ASternKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("up");
        assertTrue(aSternKI.upPressed);
    }

    @Test
    public void test_pressRichtung_upleft() {
        AKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("up+left");
        assertTrue(aSternKI.upPressed && aSternKI.leftPressed);
    }

    @Test
    public void test_pressRichtung_upright() {
        AKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("up+right");
        assertTrue(aSternKI.upPressed && aSternKI.rightPressed);
    }
    @Test
    public void test_pressRichtung_downright() {
        AKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("down+right");
        assertTrue(aSternKI.downPressed && aSternKI.rightPressed);
    }

    @Test
    public void test_pressRichtung_downleft() {
        AKI aSternKI = new ASternKI();
        aSternKI.pressRichtung("down+left");
        assertTrue(aSternKI.downPressed && aSternKI.leftPressed);
    }

    @Test
    public void test_saveMapTile_doNothing() {
        AKI asternKI = new ASternKI();
        asternKI.manager.posX = 0;
        asternKI.manager.posY = 0;
        asternKI.saveMapTileX = 0;
        asternKI.saveMapTileY = 0;
        asternKI.saveMapTile("right");

        assertEquals(0, asternKI.saveMapTileX);
        assertEquals(0, asternKI.saveMapTileY);
    }

    @Test
    public void test_saveMapTile() {
        AKI asternKI = new ASternKI();
        asternKI.manager.posX = 500;
        asternKI.manager.posY = 0;
        asternKI.saveMapTileX = 0;
        asternKI.saveMapTileY = 0;
        asternKI.saveMapTile("right");

        assertEquals(10, asternKI.saveMapTileX);
        assertEquals(0, asternKI.saveMapTileY);
    }

    @Test
    public void test_checkStrasse_nicht_erlaubt() {
        AKI aSternKI = new ASternKI();
        boolean actual = aSternKI.checkStrasse(0, 0);
        assertFalse(actual);
    }

    @Test
    public void test_checkStrasse_erlaubt_Strasse() {
        AKI asternKI = new ASternKI();
        boolean actual = asternKI.checkStrasse(1000, 150);
        assertTrue(actual);
    }

    @Test
    public void test_checkStrasse_erlaubt_Ziel() {
        AKI asternKI = new ASternKI();
        boolean actual = asternKI.checkStrasse(500, 750);
        assertTrue(actual);
    }

    @Test
    public void test_checkStrasse_erlaubt_Startposition() {
        AKI asternKI = new ASternKI();
        boolean actual = asternKI.checkStrasse(550, 750);
        assertTrue(actual);
    }

}



