package com.mapeditor.controller;

/**
 * Exceptions, die mit der Verarbeitung der Tabelle zu tun haben.
 */
public class MapException extends Exception {
    /**
     * Erzeugt neue Instanz.
     * @param error Aufgetretener Fehler.
     */
    public MapException(String error) {
        super(error);
    }
}
