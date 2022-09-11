package com.mapeditor.controller;

/**
 * Stellt den Zustand dar (Memento-Pattern)
 */
public class Originator {
    /**
     * Spielfeld-Daten
     */
    private Object[][] data;
    /**
     * Startpunkte
     */
    private String startpunkte;
    /**
     * Checkpunkte
     */
    private String checkpunkte;
    /**
     * ID des vorherigen Zustands
     */
    private int lastUndoSavepoint;
    /**
     * Verwalter der Zustaende
     */
    Caretaker caretaker;

    /**
     * Erzeugt neuen Zustand
     * @param data Neues Spielfeld
     * @param startpunkte Neue Startpunkte
     * @param checkpunkte Neue Checkpunkte
     * @param caretaker Verwalter der Zustaende
     */
    public Originator(Object[][] data, String startpunkte, String checkpunkte, Caretaker caretaker) {
        this.data = data;
        this.checkpunkte = checkpunkte;
        this.startpunkte = startpunkte;

        this.caretaker = caretaker;

        createSavepoint();
    }

    /**
     * Ueberschreibt Checkpunkte
     * @param checkpunkte Zu verwendende Checkpunkte
     */
    public void setCheckpunkte(String checkpunkte) {
        this.checkpunkte = checkpunkte;
    }

    /**
     * Ueberschreibt das Spielfeld
     * @param data Zu verwendendes Spielfeld
     */
    public void setData(Object[][] data) {
        this.data = data;
    }

    /**
     * Ueberschreibt die Startpunkte
     * @param startpunkte Zu verwendende Startpunkte
     */
    public void setStartpunkte(String startpunkte) {
        this.startpunkte = startpunkte;
    }

    /**
     * Holt aktuelle Startpunkte.
     * @return Aktuelle Startpunkte.
     */
    public String getStartpunkte() {
        return startpunkte;
    }

    /**
     * Holt aktuelle Checkpunkte.
     * @return Aktuelle Checkpunkte.
     */
    public String getCheckpunkte() {
        return checkpunkte;
    }

    /**
     * Holt aktuelles Spielfeld.
     * @return Aktuelles Spielfeld.
     */
    public Object[][] getData() {
        return data;
    }

    /**
     * Erstellt einen Sicherungspunkt des Zustands
     */
    public void createSavepoint() {
        caretaker.saveMemento(new Memento(this.data, this.checkpunkte, this.startpunkte));
        lastUndoSavepoint = caretaker.getMementoID() - 1;
    }

    /**
     * Setzt den aktuellen Zustand auf den zuletzt bekannten Zustand zurueck
     * @return
     */
    public boolean undo() {
        if(lastUndoSavepoint > 0) {
            setOriginatorZustand(lastUndoSavepoint);
            caretaker.setMementoID(lastUndoSavepoint);
            lastUndoSavepoint--;
            return true;
        }
        return false;
    }

    /**
     * Setzt den Zustand auf den nächsten Memento (aktuelle MementoID + 1)
     * @return True: Vorgang moeglich, False: Aktueller Memento ist neuester Memento
     */
    public boolean restore() {
        if(caretaker.getZustaende().size() >= lastUndoSavepoint + 2) {
            setOriginatorZustand(lastUndoSavepoint + 2);
            caretaker.setMementoID(lastUndoSavepoint + 2);
            lastUndoSavepoint++;
            return true;
        }
        return false;
    }

    /**
     * Setzte den Zustand auf einen vorherigen Zustand zurueck
     * @param mementoID ID des zu setzenden Zustands
     */
    private void setOriginatorZustand(int mementoID) {
        Memento memento = caretaker.getMemento(mementoID);

        this.data = memento.getData();
        this.checkpunkte = memento.getCheckpunkte();
        this.startpunkte = memento.getStartpunkte();
    }

    /**
     * Holt aktuellen letzten Undo-Savepoint.
     * @return Aktuellen letzten Undo-Savepoint.
     */
    public int getLastUndoSavepoint() {
        return lastUndoSavepoint;
    }

    /**
     * Konvertiert das Spielfeld zu String
     * @return Konvertiertes Spielfeld
     */
    public String dataToString() {
        String print = "";
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[i].length; j++) {
                print += ((Object[])data[i][j])[0] + " ";
            }
            print += "\n";
        }

        return print;
    }
}
