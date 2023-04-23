package anwendungsschicht;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CLITest {
    @Test
    public void checkIfOk_Server_noConfig() throws ParseException {
        String[] args = new String[] {"-in", "Server"};
        CLI.initOptions(args);
        assertFalse(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_Server() throws ParseException {
        String[] args = new String[] {"-in", "Server", "-c", "someConfig"};
        CLI.initOptions(args);
        assertTrue(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_noIP() throws ParseException {
        String[] args = new String[] { "-in", "Client", "-p", "8080"};
        CLI.initOptions(args);
        assertFalse(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_noPort() throws ParseException {
        String[] args = new String[] { "-in", "Client", "-i", "localhost"};
        CLI.initOptions(args);
        assertFalse(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_Mensch() throws ParseException {
        String[] args = new String[] { "-in", "Client", "-i", "localhost", "-p", "8080", "-m"};
        CLI.initOptions(args);
        assertTrue(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_KI() throws ParseException {
        String[] args = new String[] { "-in", "Client", "-i", "localhost", "-p", "8080", "-ki", "Trivial"};
        CLI.initOptions(args);
        assertTrue(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_Replay() throws ParseException {
        String[] args = new String[] { "-in", "Client", "-i", "localhost", "-p", "8080", "-r", "someConfig"};
        CLI.initOptions(args);
        assertTrue(CLI.checkIfOk());
    }

    @Test
    public void checkIfOk_UnknownInstance() throws ParseException {
        String[] args = new String[] { "-in", "DieseArtWidNieVernwedet"};
        CLI.initOptions(args);
        assertFalse(CLI.checkIfOk());
    }
}
