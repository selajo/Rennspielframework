package com.mapeditor.model;

import com.mapeditor.controller.TileController;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class TileReaderTest {
    int tileBreite = TileController.getInstance().tileBreite;

    @Test
    public void test_Konstruktor() {
        TileReader tileReader = new TileReader("test");
        assertEquals("test", tileReader.jsonFile);
    }

    @Test
    public void test_defaultTile_weisses_Bild() {
        TileReader tileReader = new TileReader("test");
        BufferedImage img = tileReader.defaultTile();

        for (int y = 0; y < tileBreite; y++) {
            for (int x = 0; x < tileBreite; x++) {
                Color weiss = new Color(245, 245, 245);
                int actual = img.getRGB(x, y);
                assertEquals(weiss.getRGB(), actual);
            }
        }
    }


}
