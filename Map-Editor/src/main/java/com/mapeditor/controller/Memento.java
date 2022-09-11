package com.mapeditor.controller;


/**
 * Stellt Zustaende fuer das Mememento-Pattern dar.
 * Zu einem Zustand zaehlen: Data (fuer die Tabelle), Checkpunkte und Startpunkte
 */
public class Memento {
    /**
     * Daten des Spielfeldes.
     */
    private Object[][] data;
    /**
     * Daten der Checkpunkte.
     */
    private String checkpunkte;
    /**
     * Daten der Startpositionen.
     */
    private String startpunkte;

    /**
     * Erzeugt einen Memento.
     * @param data Daten des Spielfeldes.
     * @param checkpunkte Daten der Checkpunkte.
     * @param startpunkte Daten der Startpunkte.
     */
    public Memento(Object[][] data, String checkpunkte, String startpunkte) {
        this.data = data;
        this.checkpunkte = checkpunkte;
        this.startpunkte = startpunkte;
    }

    /**
     * Holt die aktuellen Checkpunkte in Form x y ID.
     * @return Aktuellen Checkpunkte.
     */
    public String getCheckpunkte() {
        return String.valueOf(checkpunkte);
    }

    /**
     * Holt das aktuelle Spielfeld.
     * @return Aktuelles Spielfeld.
     */
    public Object[][] getData() {
        Object[][] ret = new Object[data.length][];

        for (int i = 0; i < data.length; i++) {
            ret[i] = new Object[data[i].length];
            for (int j = 0; j < data[i].length; j++) {
                ret[i][j] = data[i][j];
            }
        }

        return ret;
    }

    /**
     * Holt die aktuellen Startpunkte.
     * @return Aktuelle Startpunkt.
     */
    public String getStartpunkte() {
        return String.valueOf(startpunkte);
    }
}
