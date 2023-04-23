package com.mapeditor.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaretakerTest {
    Object[][] data = new Object[][]{{1}, {2}};
    String checkpunkte = "checkp1";
    String startpunkte = "start1";

    @Test
    public void test_saveMemento_MementoID() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);
        Caretaker caretaker = new Caretaker();
        caretaker.saveMemento(memento);

        assertEquals((Integer)1, caretaker.getMementoID());
    }

    @Test
    public void test_saveMemento_UeberschuessigeMementos() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);
        Caretaker caretaker = new Caretaker();
        caretaker.saveMemento(memento);
        caretaker.saveMemento(memento);
        caretaker.saveMemento(memento);
        caretaker.setMementoID(1);
        caretaker.saveMemento(memento);

        assertEquals(2, caretaker.getZustaende().size());
    }

    @Test
    public void test_getLastMemento_MementoValue() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);
        Caretaker caretaker = new Caretaker();
        caretaker.saveMemento(memento);
        Memento actual = caretaker.getMemento(1);

        assertEquals("checkp1", actual.getCheckpunkte());
    }

    @Test
    public void test_clearZustaende_Map() {
        Memento memento = new Memento(data, checkpunkte, startpunkte);
        Caretaker caretaker = new Caretaker();
        caretaker.saveMemento(memento);

        caretaker.clearZustaende();

        assertTrue(caretaker.getZustaende().isEmpty());
    }



}
