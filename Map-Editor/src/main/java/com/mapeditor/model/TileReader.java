package com.mapeditor.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.mapeditor.controller.TileController;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;

/**
 * Ist fuer das Einlesen der Tiles & Konfigurationsdatei sowie Speichern
 * der neuen Konfigurationsdatei zustaendig.
 */
public class TileReader {

    /**
     * Pfad zur Konfigurationsdatei.
     */
    final String jsonFile;
    Logger logger = Logger.getLogger("mapeditor");

    /**
     * Instanz des TileControllers, die notwendige Verwaltungs-Funkionalitaeten besitzt.
     */
    TileController tileController = TileController.getInstance();

    /**
     * Inhalt der Konfig-Datei.
     */
    JSONObject configFile;

    /**
     * Erzeugt TileReader. Pfad zur Konfigurationsdatei wird gesetzt.
     *
     * @param jsonFile Konfigurationsdatei im JSON-Format.
     */
    public TileReader(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    /**
     * Maximale Spaltenanzahl des Spielfeldes.
     */
    public final int maxBildschirmSpalten = 30;
    /**
     * Maximale Zeilenanzahl des Spielfeldes.
     */
    public final int maxBildschirmZeilen = 20;

    /**
     * Tileinformationen (Meta) aus Konfigurationsdatei einlesen.
     *
     * @return Tileinformationen aus der Konfigurationsdatei.
     */
    public HashMap<Integer, Object[]> tilesEinlesen() {
        HashMap<Integer, Object[]> tileInformationen = new HashMap<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            try {
                String path = "/" + Paths.get(jsonFile).getFileName().toString();
                InputStream is = getClass().getResourceAsStream(path);
                String result = new BufferedReader(new InputStreamReader(is))
                        .lines().collect(Collectors.joining("\n"));

                jsonObject = (JSONObject) jsonParser.parse(result);
            }
            catch(Exception e) {
                e.printStackTrace();
                jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFile));
            }
            configFile = jsonObject;
            JSONArray jarray = (JSONArray) jsonObject.get("KartenTile");

            for (int i = 0; i < jarray.size(); i++) {
                JSONObject o = (JSONObject) jarray.get(i);

                Object[] tileWerte = new Object[5];
                long tileNummerLong = (long) o.get("nummer");
                int tileNummerInt = (int) tileNummerLong;

                tileWerte[0] = (String) o.get("name");
                tileWerte[1] = (String) o.get("pfad");
                tileWerte[2] = Double.parseDouble((String) o.get("reibung"));
                tileWerte[3] = Boolean.parseBoolean((String) o.get("collision"));
                tileWerte[4] = Boolean.parseBoolean((String) o.get("ziel"));

                tileInformationen.put(tileNummerInt, tileWerte);

            }
        } catch (FileNotFoundException e) {
            logger.warning("Config File Nicht Gefunden!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tileInformationen;
    }

    /**
     * Erstellt eine weisse Tile.
     *
     * @return Weisse Tile.
     */
    public BufferedImage defaultTile() {
        TileController tileController = TileController.getInstance();

        BufferedImage img = new BufferedImage(tileController.tileBreite, tileController.tileBreite, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < tileController.tileBreite; y++) {
            for (int x = 0; x < tileController.tileBreite; x++) {
                Color weiss = new Color(245, 245, 245);
                img.setRGB(x, y, weiss.getRGB());
            }
        }

        return img;
    }

    /**
     * Liest die Tile-Bilder ein, die in der Konfigurationsdatei angegeben sind.
     *
     * @return Tile-Bilder.
     */
    public HashMap<Integer, BufferedImage> ladeTileBilder() {
        TileController tileController = TileController.getInstance();
        HashMap<Integer, BufferedImage> spielfeldTiles = new HashMap<>();

        //Default-Tile
        spielfeldTiles.put(tileController.defaultTile, defaultTile());

        for (Map.Entry<Integer, Object[]> entry : tileController.tileInformationen.entrySet()) {
            Object[] o = entry.getValue();
            String pfad = (String) o[1];
            logger.config(pfad);
            try {
                spielfeldTiles.put(entry.getKey(), ImageIO.read(getClass().getResource(pfad)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return spielfeldTiles;
    }

    /**
     * Schreibt den angegebenen Text in eine neue Datei.
     *
     * @param text Zu schreibender Text.
     * @return True: Datei konnte erstellt und beschrieben werden.
     * False: Datei existiert bereits oder ein Fehler ist aufgetreten
     */
    public boolean writeToFile(String path, String text) {
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(path);
                writer.write(text);
                writer.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }

    /**
     *<SPIELFELD> des Konfig-Templates wird mit der richtigen Datei ersetzt.
     * @param mapName Namen der zu speichernden Map-Datei.
     */
    public void overwriteConfig(String mapName) {
        String spielfeld = (String) configFile.get("spielfeld");
        spielfeld = spielfeld.replaceAll("<SPIELFELD>", mapName);
        configFile.put("spielfeld", spielfeld);
    }

    /**
     * Speichert die Config-Datei des Spielfeldes ab.
     * @param mapName Namen der zu speichernden Map-Datei.
     * @return True: Speichern erfolgreich; False: Andernfalls.
     */
    public boolean saveConfig(String mapName) {
        overwriteConfig(mapName);
        String fileName = "Config" + mapName + ".json";
        return writeToFile(fileName, configFile.toString());
    }

    /**
     * Ein existierendes Spielfeld von Disk einlesen und in 2D-Array konvertieren.
     * Check- und Startpunkte werden hierbei auch gesetzt.
     * @param file Pfad zur Datei
     * @return Konvertiertes Spielfeld als 2D-Array
     */
    public int[][] ladeMap(String file) {
        tileController = TileController.getInstance();
        int[][] mapTileNum = new int[tileController.maxBildschirmZeilen][tileController.maxBildschirmSpalten];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = " ";
            line = br.readLine();

            //Einlesen der Spaltenanzahl und Zeilenanzahl
            line = br.readLine();
            System.out.println(line);
            String numb[] = line.split(" ");
            Object obj[] = new Object[numb.length];
            for (int i = 0; i < 2; i++) {
                obj[i] = Integer.parseInt(numb[i]);
            }
            tileController.maxBildschirmSpalten = (int) obj[0];
            tileController.maxBildschirmZeilen = (int) obj[1];

            mapTileNum = new int[tileController.maxBildschirmZeilen][tileController.maxBildschirmSpalten];

            int row = 0;
            int num;

            //Iteriere ueber Dateiinhalt
            while (row < tileController.maxBildschirmZeilen) {
                line = br.readLine();
                String numbers[] = line.split(" ");

                for(int col = 0; col < tileController.maxBildschirmSpalten; col++) {
                    num = Integer.parseInt(numbers[col]);
                    mapTileNum[row][col] = num;
                }
                row++;
            }


            //Lese die Startpostionen ein
            line = br.readLine();
            if (!line.equals("STARTPOINT")) {
                System.out.println("Es gibt einen Fehler bei den Startpositionen im ConfigFile");
            } else {
                String startpunkt = "";
                while (true) {
                    line = br.readLine();
                    if (line == "" || line == null || line.equals("CHECKPOINT")) {
                        break;
                    }
                    String numbers[] = line.split(" ");
                    for (int i = 0; i < 2; i++) {
                        startpunkt += "" + Integer.parseInt(numbers[i]) + " ";
                    }

                    startpunkt += "" + Integer.parseInt(numbers[2]) + "\n";
                }
                tileController.setStartposition(startpunkt);
            }

            //Einlesen der CHECKPUNKTE
            if (!line.equals("CHECKPOINT")) {
                System.out.println("Es gibt einen Fehler bei den CKECkPOINTS im ConfigFile");
            } else {
                String checkpunkte = "";
                while (true) {

                    line = br.readLine();
                    if (line == "" || line == null) {
                        break;
                    }
                    String numbers[] = line.split(" ");
                    for (int i = 0; i < numbers.length; i++) { //Lese das Array aus in ein Int Array
                        checkpunkte += Integer.parseInt(numbers[i]) + " ";
                    }
                    checkpunkte += "\n";

                }
                tileController.setCheckpunkte(checkpunkte);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return mapTileNum;
        }
    }
}
