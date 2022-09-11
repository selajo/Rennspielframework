package com.mapeditor.controller;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class OriginatorTest {
    Object[][] data = new Object[][]{{1}, {2}};
    String checkpunkte = "checkp1";
    String startpunkte = "start1";

    @Test
    public void test_createSavepoint_lastUndo() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);

        assertEquals(0, originator.getLastUndoSavepoint());
    }

    @Test
    public void test_createSavepoint_pruefe_Memento_checkpunkte() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);

        assertEquals(originator.getCheckpunkte(), caretaker.getMemento(1).getCheckpunkte());
    }

    @Test
    public void test_createSavepoint_pruefe_Memento_startpunkte() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);

        assertEquals(originator.getStartpunkte(), caretaker.getMemento(1).getStartpunkte());
    }

    @Test
    public void test_createSavepoint_pruefe_Memento_data() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);

        assertTrue(Arrays.deepEquals(originator.getData(), caretaker.getMemento(1).getData()));
    }

    @Test
    public void test_createSavepoint_doppelt() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);
        originator.createSavepoint();

        assertEquals(1, originator.getLastUndoSavepoint());
    }


    @Test
    public void test_undo_initial() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);
        boolean actual = originator.undo();

        assertEquals(0, originator.getLastUndoSavepoint());
    }


    @Test
    public void test_undo_lastUndoPoint() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);
        originator.setStartpunkte("test2");
        originator.createSavepoint();
        boolean actual = originator.undo();

        assertEquals(0, originator.getLastUndoSavepoint());
    }

    @Test
    public void test_undo_mit_Save_startpunkt() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);
        originator.createSavepoint();
        originator.setStartpunkte("test2");

        boolean actual = originator.undo();

        assertEquals("start1", originator.getStartpunkte());
    }

    @Test
    public void test_dataToString_return() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);

        originator.setCheckpunkte("checker");
        originator.setCheckpunkte("starter");
        originator.setData(new Object[][]{{new Object[]{1}}, {new Object[]{2}}});

        String actual = originator.dataToString();

        assertEquals("1 \n2 \n", actual);
    }

    @Test
    public void test_restore_unzulaessig() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);

        Boolean actual = originator.restore();

        assertFalse(actual);
    }

    @Test
    public void test_restore_zulaessig() {
        Caretaker caretaker = new Caretaker();
        Originator originator = new Originator(data, startpunkte, checkpunkte, caretaker);
        originator.createSavepoint();
        originator.createSavepoint();
        originator.undo();

        Boolean actual = originator.restore();

        assertTrue(actual);
    }
}
