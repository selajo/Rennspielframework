package com.mapeditor.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;


public class TileTableModelTest {
    Object[][] data;
    Object[] value;

    void initData() {
        value = new Object[]{0, new BufferedImage(1, 1, 1)
        };
        data = new Object[20][30];
        for (int i = 0; i < 20; i++) {
            Arrays.fill(data[i], value);
        }
    }

    @Test
    public void test_getValueAt_return_second_value() {
        initData();
        TileTableModel model = new TileTableModel(data, new String[]{"1"});
        Object actual = model.getValueAt(0, 0);
        assertEquals(value[1], actual);
    }

    @Test
    public void test_set_and_getComp_ValueAt() {
        initData();
        TileTableModel model = new TileTableModel(data, new String[]{"1"});
        model.setValueAt(new Object[]{1, 2}, 1, 1);

        Object[] actual = model.getCompleteValueAt(1, 1);
        assertEquals(1, actual[0]);
    }

    @Test
    public void test_getColumnClass_type() {
        initData();
        TileTableModel model = new TileTableModel(data, new String[]{"1"});

        Class actual = model.getColumnClass(0);
        assertEquals(Icon.class, actual);
    }

    @Test
    public void test_setValueAtRange() {
        initData();
        TileTableModel model = new TileTableModel(data, new String[]{"1"});
        Object[] data = new Object[]{1, 2};
        int[] rows = new int[] {1, 2};
        int[] cols = new int[] {1, 2};
        model.setValueAtRange(data, rows, cols);

        assertEquals(1, model.getCompleteValueAt(1, 1)[0]);
        assertEquals(1, model.getCompleteValueAt(1, 2)[0]);
        assertEquals(1, model.getCompleteValueAt(2, 1)[0]);
        assertEquals(1, model.getCompleteValueAt(2, 2)[0]);

    }
}
