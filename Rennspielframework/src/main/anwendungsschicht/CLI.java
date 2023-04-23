package anwendungsschicht;

import org.apache.commons.cli.*;


public class CLI {
    private static CommandLine cli;
    static Options options;

    static String serverArg = "server";
    static String clientArg = "client";

    public static void initOptions(String[] args) throws ParseException {
        options = new Options();
        options.addRequiredOption("in", "instanz", true, "Angabe, ob Server oder Client");
        options.addOption("c", "config", true, "Server: Pfad zur Konfiguraationsdatei fuer den Server");
        options.addOption("p", "port", true, "Client: Port zur Verbindung mit dem Server");
        options.addOption("i", "ip", true, "Client: IP zur Verbindung mit dem Server");
        options.addOption("m", "mensch", false, "Client: Spieler ist ein Mensch");
        options.addOption("r", "replay", true, "Client: Es wird ein Replay ausgefuehrt");
        options.addOption("ki", "ki", true, "Client: Spieler ist eine KI");
        options.addOption("a", "auto", true, "Client: Autoauswahl (1: rot, 2: blau, 3: gruen, 4: gelb)");
        options.addOption("headless", "headless", false, "Server: Started keine grafische Ansicht");

        CommandLineParser parser = new DefaultParser();
        cli = parser.parse(options, args);
    }

    public static String getInstanz() {
        return cli.getOptionValue("in");
    }

    public static String getConfig() {
        if(cli.hasOption("c")) {
            return cli.getOptionValue("c");
        }
        return null;
    }

    public static Integer getPort() {
        if(cli.hasOption("p")) {
            return Integer.valueOf(cli.getOptionValue("p"));
        }
        return null;
    }

    public static String getIP() {
        if(cli.hasOption("i")) {
            return cli.getOptionValue("i");
        }
        return null;
    }

    public static String getKIArt() {
        if(cli.hasOption("ki")) {
            return cli.getOptionValue("ki");
        }
        return null;
    }

    public static String getReplay() {
        if(cli.hasOption("r")) {
            return cli.getOptionValue("r");
        }
        return null;
    }

    public static int getCarType() {
        if(cli.hasOption("a")) {
            return Integer.parseInt(cli.getOptionValue("a"));
        }
        return 0;
    }

    public static boolean isMensch() {
        return cli.hasOption("m");
    }

    public static boolean isHeadless() { return cli.hasOption("headless"); }

    public static boolean isKI() {
        return cli.hasOption("ki");
    }

    public static boolean isServer() {
        return cli.getOptionValue("in").toLowerCase().contentEquals(serverArg);
    }

    public static boolean isClient() {
        return cli.getOptionValue("in").toLowerCase().contentEquals(clientArg);
    }

    public static boolean isReplay() {
        return cli.hasOption("r");
    }

    public static boolean checkIfOk() {
        String instance = getInstanz();
        if(isServer()) {
            if(getConfig() != null) {

                return true;
            }
            return false;
        }
        else if(isClient()) {
            if(!cli.hasOption("p") || !cli.hasOption("i")|| cli.hasOption("headless")) {
                return false;
            }

            if(isMensch()) {
                return true;
            }
            else if(isKI()) {
                return getKIArt() != null;
            }
            else if(isReplay()) {
                return getReplay() != null;
            }
        }
        return false;
    }

    public static void printHelp(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Rennspiel", options);
    }
}
