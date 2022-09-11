package spielansichtsschicht;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;

import javax.swing.*;

/**
 * Alle grafischen Spielfeldeigenschaften werden hier verwaltet, Spielfeldstruktur, sowie die Bilder der Tiles
 *
 * @author André
 */
public class SpielFeldManager {

    public BufferedImage combinedImage = null;

    Spieloptionen optionen = Spieloptionen.getInstance();
    /**
     * Array mit allen Tiles, die grafische Eigenschaften definieren
     */
    public Tile[] tile;
    /**
     * int [][] das die Struktur der Spielkarte besitzt
     */
    public int mapTileNum[][] = null;

    public Controller controller;

    public void addController(Controller controller) {
        this.controller = controller;
        tile = new Tile[100]; //Maximal 100 verschiedene Tiles

        //getTileImage();
        convertTileImage();
        loadMap(controller.spielDaten.world);

    }

    public SpielFeldManager() {
    }


    /**
     * Lade die Spielkarte aus den Spieloptionen
     *
     * @param filePath
     */
    public void loadMap(String filePath) {
        while (optionen.mapTileNum == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        mapTileNum = new int[controller.spielDaten.maxBildschirmSpalten][controller.spielDaten.maxBildschirmZeilen]; //initialisieren eines Arrays
        mapTileNum = optionen.mapTileNum;
    }

    /**
     * Einlesen der TileBilder in die grafische Ansicht
     */
    private void convertTileImage() {
        while (optionen.spielfeldTiles == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (Map.Entry<Integer, BufferedImage> entry : optionen.spielfeldTiles.entrySet()) {
            //schreibt die Map in das Benoetigte Array
            Tile t = new Tile();
            t.image = entry.getValue();
            tile[entry.getKey()] = t;
        }


    }

    /**
     * Zeichenen der Spielkarte auf dem JPanel
     *
     * @param g2
     */
    public void draw(Graphics2D g2) {

        if (combinedImage == null) {

            int width = optionen.bildschirmBreite;
            int height = optionen.bildschirmHoehe;

            combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combinedImage.createGraphics();

            int worldCol = 0;
            int worldRow = 0;
            int x = 0;
            int y = 0;

            Map<Integer, List<TileKoordinate>> checkpoints = Spieloptionen.getInstance().checkpointListe;

            if (mapTileNum != null) {
                while (worldCol < controller.spielDaten.maxBildschirmSpalten && worldRow < controller.spielDaten.maxBildschirmZeilen) {

                    int tileNum = mapTileNum[worldCol][worldRow];
                    BufferedImage image = tile[tileNum].image;
                    g.drawImage(image, x, y, controller.spielDaten.tileGroesse, controller.spielDaten.tileGroesse, null);
                    worldCol++;
                    x += controller.spielDaten.tileGroesse;

                    if (worldCol == controller.spielDaten.maxBildschirmSpalten) {
                        worldCol = 0;
                        x = 0;
                        worldRow++;
                        y += controller.spielDaten.tileGroesse;
                    }
                }
            }

        } else {
            g2.drawImage(combinedImage, 0, 0, optionen.bildschirmBreite, optionen.bildschirmHoehe, null);
        }
    }

}
