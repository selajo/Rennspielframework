package com.mapeditor.app;

import com.mapeditor.controller.TileController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

            logger.setLevel(Level.ALL);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Liste notwendige Daten ein, erzeugt GUI und stellt diese dar.
     *
     * @param args Anzugebende Kommandozeilenparameter.
     */
    public static void main(String[] args) {
        initLogger();
        logger.info("Mapeditor wird gestartet");
        TileController tileController = TileController.getInstance();
        tileController.datenEinlesen();

        gui g = new gui();
        g.erschaffeFenster("Map-Editor");
    }

}
