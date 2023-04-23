package com.mapeditor.view;

import com.mapeditor.controller.TileController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.*;


/**
 * Liste notwendige Daten ein, erzeugt GUI und stellt diese dar.
 */
public class App {
    /**
     * Loggt die Ausgaben.s
     */
    static Logger logger = Logger.getLogger("mapeditor");

    /**
     * Initialisiert den Logger des Programms.
     */
    public static void initLogger() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
            Date date = new Date();
            Handler fileHandler = new FileHandler(simpleDateFormat.format(date) + "mapeditor.log");
            Formatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void PrintHelp() {
        System.out.print("Folgende Parameter werden akzeptier:\n" +
                "-h:    Liefert diese Ausgabe\n" +
                "-v:    Log-Ausgaben werden nicht mehr gemacht\n");
    }

    public static void ReadArgs(String[] args) {
        List list = Arrays.asList(args);
        if(list.contains("-h")) {

        }
        if(list.contains("-v")) {
            logger.setLevel(Level.SEVERE);
        }
    }

    /**
     * Liste notwendige Daten ein, erzeugt GUI und stellt diese dar.
     *
     * @param args Anzugebende Kommandozeilenparameter.
     */
    public static void main(String[] args) {
        if(args.length > 0) {
            ReadArgs(args);
        }
        else {
            initLogger();
            logger.setLevel(Level.ALL);
        }
        logger.info("Mapeditor wird gestartet");
        TileController tileController = TileController.getInstance();
        tileController.datenEinlesen();

        gui g = new gui();
        g.erschaffeFenster("Map-Editor");
    }

}
