package com.mapeditor.controller;

import com.mapeditor.model.TileReader;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Beinhaltet die Informationen zu den Tiles, sowie Tilegroesse und Tile-Bilder.
 */
public class TileController {
    /**
     * Breite der Tiles in Pixel.
     */
    public final int tileBreite = 16;
    /**
     * ID der Default-Tile, mit der die Tabelle initialisiert wird.
     */
    public final int defaultTile = -1;
    /**
     * Tile-IDs, die das Ziel darstellen.
     */
    public final int[] ziel = new int[]{45, 46, 47};

    /**
     * ID der Grass-Tiles
     */
    public final int grassID = 20;
    /**
     * Ab diesem Flag beginnt das Spielfeld.
     */
    public final String FLAG_map = "MAP\n";
    /**
     * Ab diesem Flag beginnen die Checkpunkte.
     */
    public final String FLAG_checkpunkte = "CHECKPOINT\n";
    /**
     * Ab diesem Flag beginnen die Startpunkte.
     */
    public final String FLAG_startpos = "STARTPOINT\n";

    /**
     * Aktuelle Anzahl an Checkpunkten
     */
    int checkpunktCounter = 0;

    /**
     * Aktuelle Startpositionen
     */
    String startposition;

    /**
     * Aktuelle Checkpunkte
     */
    String checkpunkte;
    /**
     * Pfad zur Konfigurationsdatei.
     */
    final String jsonFile = "Res/TileInfo.json";
    /**
     * Informationen (Meta) zu den Tiles.
     */
    public Map<Integer, Object[]> tileInformationen = new HashMap<>();
    /**
     * Bilder der Tiles mit zugeordneter ID.
     */
    public HashMap<Integer, BufferedImage> spielfeldTiles = new HashMap<>();
    /**
     * TileReader-Instanz, um Dateien zu manipulieren.
     */
    TileReader tileReader = new TileReader(jsonFile);


    /**
     * Erzeugt Instanz. Nach Singleton-Muster.
     */
    private static final TileController tileController = new TileController();

    /**
     * Liefert die bereits erstelle Instanz. Nach Singleton-Muster.
     * @return Aktuelle Instanz von TileController
     */
    public static TileController getInstance() {
        return tileController;
    }

    /**
     * Erzeugt Instanz (nicht aufrufbar). Nach Singleton-Muster.
     */
    private TileController() {
        this.checkpunkte = "";
        this.startposition = "";
    }

    /**
     * Checkpunkte setzen
     * @param checkpunkte Zu setzende Checkpunkte
     */
    public void setCheckpunkte(String checkpunkte) {
        this.checkpunkte = checkpunkte;
    }

    /**
     * Startpositionen setzen
     * @param startposition Zu setzende Startpositionen
     */
    public void setStartposition(String startposition) {
        this.startposition = startposition;
    }

    /**
     * Liefert aktuelle Checkpunkte
     * @return Aktuelle Checkpunkte
     */
    public String getCheckpunkte() {
        return checkpunkte;
    }

    /**
     * Liefert aktuelle Startpositionen
     * @return Aktuelle Startpositionen
     */
    public String getStartposition() {
        return startposition;
    }

    /**
     * Liest die TileInformationen und SpielfeldTiles ein.
     */
    public void datenEinlesen() {
        this.tileInformationen = tileReader.tilesEinlesen();
        this.spielfeldTiles = tileReader.ladeTileBilder();
    }

    /**
     * Schreibt Spielfeld, Checkpunkte und Startpositionen zu File via TileReader.
     * @param map Zu schreibendes Spielfeld.
     * @param mapName Namen des Spielfeldes
     * @return True: Dateien wurden erfolgreich erzeugt und beschrieben.
     * False: Datei existiert bereits oder ein Fehler ist aufgetreten.
     */
    public boolean writeCompleteData(String mapName, String map) {
        String completeMap =    FLAG_map + map +
                                FLAG_startpos + startposition +
                                FLAG_checkpunkte + checkpunkte;
        boolean mapRet = tileReader.writeToFile(mapName + ".txt", completeMap);
        boolean configRet = tileReader.saveConfig(mapName);

        return mapRet && configRet;
    }

    /**
     * Lade existierendes Spielfeld von Disk
     * @param file Zu ladendes Spielfeld
     * @return Spielfeld als 2D-Array
     */
    public int[][] ladeMap(String file) {
        return tileReader.ladeMap(file);
    }

    /**
     * Fuege neue Startpunkte hinzu
     * @param rows X-Koordinaten der Startpunkte
     * @param cols Y-Koordinaten der Startpunkte
     * @param richtung Richtung der Startpunkte
     */
    public void addStartpunkte(int[] rows, int[] cols, int richtung) {
        for (int col = 0; col < cols.length; col++) {
            for (int row = 0; row < rows.length; row++) {
                startposition += (cols[col]) + " " + (rows[row]) + " " + richtung + "\n";
            }
        }
    }

    /**
     * Fuege neue Checkpunkte hinzu
     * @param rows X-Koordinate der Checkpunkte
     * @param cols Y-Koordinate der Checkpunkte
     */
    public void addCheckpunkte(int[] rows, int[] cols) {
        checkpunktCounter++;

        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < cols.length; col++) {
                checkpunkte += (cols[col] + 1) + " " + (rows[row] + 1) + " " + checkpunktCounter + "\n";
            }
        }
    }

}
