package com.mapeditor.controller;

import com.mapeditor.app.TileTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Verwaltet das Konvertieren einer JTable zu den korrespondierenden ASCII-Code der Tiles.
 */
public class TableConverter {

    /**
     * Besitzt wichtige Informationen zu den Tiles.
     * Bsp.: Default-Tile-ID, Ziel-Tile-IDs...
     */
    static TileController tileController = TileController.getInstance();

    /**
     * Falls beim Vorgang Convert ein Fehler auftritt, wird die Nachricht hier gespeichert.
     */
    public static String errorMessage = "";

    /**
     * Der aktuelle Zustand des Spielfeldes.
     */
    public static Originator originator;


    /**
     * Konvertiert die eingegebene Tabelle zu den IDs der Tiles.
     * Ergebnis wird als String wiedergegeben.
     * Fehlende Informationen werden gesammelt.
     *
     * @param tabelle Tabelle, die zu konvertieren ist.
     * @return Fehlende Informationen
     */
    public static String convert(JTable tabelle) throws MapException {
        String map = "";
        boolean ziel, start;
        boolean tileFehlt = false;
        ziel = false;
        TileTableModel model = (TileTableModel) tabelle.getModel();

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 30; col++) {
                Object[] value = model.getCompleteValueAt(row, col);
                int tile = (int) value[0];

                //Leere Tile gefunden
                if (tile == tileController.defaultTile && !tileFehlt) {
                    errorMessage += "Mindestens eine Koordinate fehlt. " +
                            "Z.B. (" + (row + 1) + ", " + (col + 1) + ")\n";
                    tileFehlt = true;
                }

                //Ziel gefunden
                if (Arrays.stream(tileController.ziel).anyMatch(i -> i == tile)) {
                    ziel = true;
                }

                map += tile + " ";
            }
            map += "\n";
        }

        //Das Ziel fehlt
        if (!ziel) {
            errorMessage += "Das Ziel fehlt.\n";
        }
        //Start fehlt
        if(tileController.startposition == "") {
            errorMessage += "Die Startpositionen muessen noch gesetzt werden.\n";
        }
        //Checkpunkte fehlen
        if(tileController.checkpunkte == "") {
            errorMessage += "Die Checkpunkte muessen noch gesetzt werden.\n";
        }

        return map;
    }

    /**
     * Konvertiert Tile zu ImageIcon
     * @param id ID der Tiles
     * @return Konvertiertes ImageIcon
     */
    public static ImageIcon convertTilesToIcon(int id) {
        ImageIcon icon = new ImageIcon(tileController.spielfeldTiles.get(id));
        Image image = icon.getImage();
        Image newimg = image.getScaledInstance(16 * 2, 16 * 2, Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    /**
     * Aendert die Hoehe und Breite eines BufferedImages und konvertiert dies zu ImageIcon.
     * @param input Zu aenderndes Image
     * @return Konvertiertes ImageIcon
     */
    public static ImageIcon resizeImageIcon(BufferedImage input) {
        ImageIcon image = new ImageIcon(input);
        Image img = image.getImage();
        img = img.getScaledInstance(16 * 2, 16 * 2, Image.SCALE_SMOOTH);
        image = new ImageIcon(img);
        return image;
    }

    /**
     * Fuellt noch nicht ausgefuellte Zellen der Tabelle mit anzugebenden Tiles
     * @param model Zu fuellende Tabelle
     * @param id Zu verwendende Tiles
     */
    public static void fuelleRestMit(TileTableModel model, int id) {
        ImageIcon icon = convertTilesToIcon(id);
        Object[] fuelle = new Object[]{id, icon};
        Object[][] data = new Object[20][30];

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 30; col++) {
                data[row][col] = model.getCompleteValueAt(row, col);
                if ((int)((Object[])data[row][col])[0] == tileController.defaultTile) {
                    model.setValueAt(fuelle, row, col);
                }
            }
        }

    }

    /**
     * Ueberschreibt Tabelle mit neuem Spielfeld
     * @param model Zu ueberschreibende Tabelle
     * @param map Zu verwendendes Spielfeld
     */
    public static void fuelleTabelleKomplett(TileTableModel model, int[][] map) {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 30; col++) {
                Object[] fuelle = new Object[2];
                fuelle[0] = map[row][col];
                fuelle[1] = convertTilesToIcon(map[row][col]);
                model.setValueAt(fuelle, row, col);
            }
        }
    }

}
