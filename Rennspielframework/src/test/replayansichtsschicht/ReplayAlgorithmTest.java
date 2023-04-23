package replayansichtsschicht;

import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReplayAlgorithmTest {

    @BeforeClass
    public static void setUp() {
        ReplayAlgorithm.verbose = false;
    }

    @AfterClass
    public static void tearDown() {
        ReplayAlgorithm.verbose = true;
    }

    @Test
    public void setNewTimer() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("test");
        replayAlgorithm.endTime = System.nanoTime();
        replayAlgorithm.setNewTimer("71068669072000", "71068814354300");

        assertTrue(replayAlgorithm.endTime > System.nanoTime());
    }

    @Test
    public void readConfig_size() throws IOException, ParseException {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.readConfig();

        assertEquals(33, replayAlgorithm.protokoll.size());
    }

    @Test
    public void readConfig_firstDirection() throws IOException, ParseException {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.readConfig();

        assertEquals("null", replayAlgorithm.protokoll.get(0)[0]);
    }

    @Test
    public void ermittleRichtung() throws IOException, ParseException {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.readConfig();
        replayAlgorithm.ermittleRichtung();

        assertEquals(32, replayAlgorithm.protokoll.size());
    }

    @Test
    public void pressRichtung_left() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("left");

        assertTrue(replayAlgorithm.leftPressed && !replayAlgorithm.upPressed &&
                !replayAlgorithm.rightPressed && !replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_up() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("up");

        assertTrue(!replayAlgorithm.leftPressed && replayAlgorithm.upPressed &&
                !replayAlgorithm.rightPressed && !replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_down() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("down");

        assertTrue(!replayAlgorithm.leftPressed && !replayAlgorithm.upPressed &&
                !replayAlgorithm.rightPressed && replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_right() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("right");

        assertTrue(!replayAlgorithm.leftPressed && !replayAlgorithm.upPressed &&
                replayAlgorithm.rightPressed && !replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_upleft() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("up+left");

        assertTrue(replayAlgorithm.leftPressed && replayAlgorithm.upPressed &&
                !replayAlgorithm.rightPressed && !replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_upright() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("up+right");

        assertTrue(!replayAlgorithm.leftPressed && replayAlgorithm.upPressed &&
                replayAlgorithm.rightPressed && !replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_downright() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("down+right");

        assertTrue(!replayAlgorithm.leftPressed && !replayAlgorithm.upPressed &&
                replayAlgorithm.rightPressed && replayAlgorithm.downPressed);
    }

    @Test
    public void pressRichtung_downleft() {
        ReplayAlgorithm replayAlgorithm = new ReplayAlgorithm("src/test/res/replayAlgTest.json");
        replayAlgorithm.pressRichtung("down+left");

        assertTrue(replayAlgorithm.leftPressed && !replayAlgorithm.upPressed &&
                !replayAlgorithm.rightPressed && replayAlgorithm.downPressed);
    }

}
