package com.mapeditor.controller;

import com.mapeditor.view.TileTableModel;
import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TableConverterTest {
    String[] columnNames;

    Object[][] blockMap = new Object[20][30];

    public TableConverterTest() {
        columnNames = new String[30];
        for (int i = 1; i <= 30; i++) {
            columnNames[i - 1] = Integer.toString(i);
        }

        Object[] grenze = new Object[]{
                10, 20
        };
        for (int i = 0; i < 20; i++) {
            Arrays.fill(blockMap[i], grenze);
        }
    }

    public void initData(Object[][] data) {
        Object[] leer = new Object[]{
                TileController.getInstance().defaultTile, null
        };
        for (int i = 0; i < 20; i++) {
            Arrays.fill(data[i], leer);
        }
    }

    @Test
    public void test_convert_Tabelle_nicht_initialisiert() {
        try {
            Object[][] data = new Object[20][30];
            TileTableModel model = new TileTableModel(data, columnNames);
            JTable jTable = new JTable(model);

            String actual = TableConverter.convert(jTable);
        } catch (Exception e) {
            assertEquals(java.lang.NullPointerException.class, e.getClass());
        }
    }

    @Test
    public void test_convert_Tabelle_ist_leer() {
        try {
            Object[][] data = new Object[20][30];
            initData(data);
            TileTableModel model = new TileTableModel(data, columnNames);
            JTable jTable = new JTable(model);

            String actual = TableConverter.convert(jTable);
        } catch (MapException mapException) {
            assertEquals("Alle Tiles muessen belegt sein. [Koordinate: (1, 1)]", mapException.getMessage());
        }
    }

    @Test
    public void test_convert_Ziel_fehlt() {
        try {
            Object[][] data = blockMap;
            TileTableModel model = new TileTableModel(data, columnNames);
            JTable jTable = new JTable(model);

            TableConverter.convert(jTable);
        } catch (MapException mapException) {
            assertTrue(mapException.getMessage().contains("Ziel"));
        }
    }

    @Test
    public void test_convert_Ziel_vorhanden_Start_fehlt() {
        try {
            Object[][] data = blockMap;
            data[2][2] = new Object[]{45, 30};
            TileController tileController = TileController.getInstance();
            tileController.startposition = "";
            TileTableModel model = new TileTableModel(data, columnNames);
            JTable jTable = new JTable(model);

            TableConverter.convert(jTable);
        } catch (MapException mapException) {
            assertTrue(mapException.getMessage().contains("Startposition"));
        }
    }

    @Test
    public void test_convert_Ziel_Start_vorhanden_Checkpunkt_fehlt() {
        try {
            Object[][] data = blockMap;
            data[2][2] = new Object[]{45, 30};
            TileController tileController = TileController.getInstance();
            tileController.startposition = "irgendwas";
            tileController.checkpunkte = "";
            TileTableModel model = new TileTableModel(data, columnNames);
            JTable jTable = new JTable(model);

            TableConverter.convert(jTable);
        } catch (MapException mapException) {
            assertTrue(mapException.getMessage().contains("Checkpunkt"));
        }
    }

    @Test
    public void test_convert_alles_vorhanden_Map_ist_in_Ordnung() {
        try {
            Object[][] data = blockMap;
            data[2][2] = new Object[]{45, 30};
            TileController tileController = TileController.getInstance();
            tileController.startposition = "irgendwas";
            tileController.checkpunkte = "irgendwas";
            TileTableModel model = new TileTableModel(data, columnNames);
            JTable jTable = new JTable(model);

            String actual = TableConverter.convert(jTable);
            assertEquals("45", actual.split("\n")[2].split(" ")[2]);
        } catch (MapException mapException) {
        }
    }

    @Test
    public void test_convertTilesToIcon_Height() {
        TileController tileController = TileController.getInstance();
        tileController.spielfeldTiles.put(0, new BufferedImage(1, 1, 1));

        ImageIcon actual = TableConverter.convertTilesToIcon(0);
        assertEquals(32, actual.getIconHeight());
    }

    @Test
    public void test_fuelleRestMit_Tabelle_nicht_initialisiert() {
        try {
            TileTableModel model = new TileTableModel(new Object[][]{}, new String[]{});
            TileController tileController = TileController.getInstance();
            tileController.spielfeldTiles.put(0, new BufferedImage(1, 1, 1));
            TableConverter.fuelleRestMit(model, 0);
        } catch (Exception e) {
            assertEquals(ArrayIndexOutOfBoundsException.class, e.getClass());
        }
    }

    @Test
    public void test_fuelleRestMit_ein_leerer_Block() {
        Object[][] data = blockMap;
        data[2][2] = new Object[]{-1, 1};

        TileTableModel model = new TileTableModel(data, new String[]{});
        TileController tileController = TileController.getInstance();
        tileController.spielfeldTiles.put(0, new BufferedImage(1, 1, 1));
        TableConverter.fuelleRestMit(model, 0);

        assertEquals(0, model.getCompleteValueAt(2, 2)[0]);
    }

    @Test
    public void test_fuelleTabelleKomplett_Tabelle_nicht_initialisiert() {
        try {
            TileTableModel model = new TileTableModel(new Object[][]{}, new String[]{});
            TileController tileController = TileController.getInstance();
            tileController.spielfeldTiles.put(0, new BufferedImage(1, 1, 1));
            TableConverter.fuelleTabelleKomplett(model, new int[][]{{0}, {0}});
        } catch (Exception e) {
            assertEquals(ArrayIndexOutOfBoundsException.class, e.getClass());
        }
    }

    @Test
    public void test_fuelleTabelleKomplett_Tabelle_in_Ordnung() {
        Object[][] data = blockMap;
        TileTableModel model = new TileTableModel(data, new String[]{});
        TileController tileController = TileController.getInstance();
        tileController.spielfeldTiles.put(0, new BufferedImage(1, 1, 1));
        int[][] map = new int[20][30];
        for (int i = 0; i < 20; i++) {
            Arrays.fill(map[i], 0);
        }

        TableConverter.fuelleTabelleKomplett(model, map);

        for(int i = 0; i < 20; i++) {
            for(int j = 0; j < 30; j++) {
                assertEquals(0, model.getCompleteValueAt(i, j)[0]);
            }
        }
    }

    @Test
    public void test_resizeImageIcon() {
        BufferedImage input = new BufferedImage(1, 1, 1);
        ImageIcon actual = TableConverter.resizeImageIcon(input);
        assertEquals(32, actual.getIconWidth());
    }
}
