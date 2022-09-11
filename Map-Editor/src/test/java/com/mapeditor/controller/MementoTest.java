package com.mapeditor.controller;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MementoTest {
    Object[][] data = new Object[][]{{1}, {2}};
    String checkpunkte = "checkp1";
    String startpunkte = "start1";

    @Test
    public void test_getCheckpunkte() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);

        assertEquals("checkp1", memento.getCheckpunkte());
    }

    @Test
    public void test_getStartpunkte() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);

        assertEquals("start1", memento.getStartpunkte());
    }

    @Test
    public void test_getData() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);

        assertTrue(Arrays.deepEquals(new Object[][]{{1}, {2}}, memento.getData()));
    }
}
