package com.mapeditor.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Speichert und stellt Zustaende des Memento-Patterns zur Verfuegung.
 */
public class Caretaker {
    /**
     * Logt die Ausgabe.
     */
    Logger logger = Logger.getLogger("mapeditor");
    /**
     * Hier werden alle erstellen Mementos gespeichert.
     */
    private final Map<Integer, Memento> zustaende = new HashMap<>();
    /**
     * ID des aktuellen Mementos.
     */
    private Integer mementoID = 0;

    /**
     * Speichert einen Memento.
     * @param memento Der zu speichernde Memento.
     */
    public void saveMemento(Memento memento) {
        mementoID += 1;
        logger.config("Speichere Zustand " + mementoID);
        zustaende.put(mementoID, memento);

        //Ueberschuessige Eintraeg loeschen -> fuer restore
        for(int i = mementoID + 1 ; i <= zustaende.size(); i++) {
            zustaende.remove(i);
        }
    }

    /**
     * Setzt die MementoID.
     * @param mementoID Zu setzende MementoID.
     */
    public void setMementoID(Integer mementoID) {
        logger.config("Zeiger bei " + mementoID);
        this.mementoID = mementoID;
    }

    /**
     * Holt den Memento zur angegebenen ID.
     * @param id ID des zu holenden Mementos.
     * @return Memento zur angegebenen ID.
     */
    public Memento getMemento(int id) {
        logger.config("Mache letzte Aktion rueckgaengig. Zustand: " + id);
        Memento memento = zustaende.get(id);
        return memento;
    }

    /**
     * Loescht alle erstellten und gespeicherten Mementos.
     */
    public void clearZustaende() {
        logger.config("Alle Zustaende werden geloescht");
        zustaende.clear();
    }

    /**
     * Holt die MementoID
     * @return Aktuelle MementoID.
     */
    public Integer getMementoID() {
        return mementoID;
    }

    /**
     * Holt alle gespeicherten Mementos.
     * @return Alle gespeicherten Mementos.
     */
    public Map<Integer, Memento> getZustaende() {
        return zustaende;
    }


}
