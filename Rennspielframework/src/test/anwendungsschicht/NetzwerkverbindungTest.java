package anwendungsschicht;

import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetzwerkverbindungTest {
    @Test
    public void test_ZeitstempelSetzen_Empty() {
        Netzwerkverbindung netzwerkverbindung = new Netzwerkverbindung("test", null);
        netzwerkverbindung.initZeitstempel();

        netzwerkverbindung.zeitstempelSetzen(1, "left");

        assertEquals("left", netzwerkverbindung.zeitstempel.get(1).get(0)[0]);
    }

    @Test
    public void test_ZeitstempelSetzen_SameDirection() {
        Netzwerkverbindung netzwerkverbindung = new Netzwerkverbindung("test", null);
        netzwerkverbindung.initZeitstempel();

        netzwerkverbindung.zeitstempelSetzen(1, "left");
        assertTrue(netzwerkverbindung.zeitstempel.get(1).size() == 1);

        netzwerkverbindung.zeitstempelSetzen(1, "left");
        assertTrue(netzwerkverbindung.zeitstempel.get(1).size() == 1);
    }

    @Test
    public void test_ZeitstempelSetzen_DiffDirection() {
        Netzwerkverbindung netzwerkverbindung = new Netzwerkverbindung("test", null);
        netzwerkverbindung.initZeitstempel();

        netzwerkverbindung.zeitstempelSetzen(1, "left");
        assertTrue(netzwerkverbindung.zeitstempel.get(1).size() == 1);

        netzwerkverbindung.zeitstempelSetzen(1, "right");
        assertTrue(netzwerkverbindung.zeitstempel.get(1).size() == 2);
        assertEquals("right", netzwerkverbindung.zeitstempel.get(1).get(1)[0]);
    }

    @Test
    public void test_ZeitstempelSetzen_DiffPlayer() {
        Netzwerkverbindung netzwerkverbindung = new Netzwerkverbindung("test", null);
        netzwerkverbindung.initZeitstempel();

        netzwerkverbindung.zeitstempelSetzen(1, "left");
        netzwerkverbindung.zeitstempelSetzen(2, "right");

        assertTrue(netzwerkverbindung.zeitstempel.get(1).size() == 1);
        assertTrue(netzwerkverbindung.zeitstempel.get(2).size() == 1);
    }

    @Test
    public void test_convertZeitstempel() {
        Netzwerkverbindung netzwerkverbindung = new Netzwerkverbindung("test", null);
        netzwerkverbindung.initZeitstempel();
        netzwerkverbindung.zeitstempelSetzen(1, "left");
        netzwerkverbindung.zeitstempelSetzen(1, "up");

        JSONObject actual = netzwerkverbindung.convertZeitstempel(1);
        assertTrue(actual.containsKey("timestamps"));
    }

    @Test
    public void test_convertDirection_upright() {
        Netzwerkverbindung netzwerkverbindung = new Netzwerkverbindung("test", null);
        String actual = netzwerkverbindung.convertDirection(true, false, false, true);
        assertEquals("up+right", actual);
    }
}
