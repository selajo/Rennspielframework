package com.mapeditor.view;

import com.mapeditor.controller.TileController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * TableModel, welche die Daten in Form eines Arrays pro Zelle darstellt.
 * Ausgegeben werden hierbei die Tile-Bilder.
 */
public class TileTableModel extends DefaultTableModel {
    /**
     * Daten der Tabelle.
     */
    private Object[][] data;
    /**
     * Instanz des TileControllers, der als Ansprechpartner zur Logik-Schicht fungiert.
     */
    public TileController tileController;

    /**
     * Erzeugt TileTableModel mit angegebenen Daten und Spaltennamen.
     * @param data Daten, die die Tabelle beinhalten soll.
     * @param columnnames Spaltennamen der Tabelle.
     */
    public TileTableModel(Object[][] data, String[] columnnames) {
        super(data, columnnames);
        tileController = TileController.getInstance();
        this.data = data;
    }

    /**
     * Liefert den zweiten Inhalt des Arrays der Zelle.
     * @param row             the row whose value is to be queried
     * @param col          the column whose value is to be queried
     * @return Zweiter Inhalt des Arrays der Zelle.
     */
    @Override
    public Object getValueAt(int row, int col) {
        Object[] value =  (Object[]) data[row][col];
        return value[1];
    }

    /**
     * Setzt den Wert an einer einzigen Zelle der Tabelle.
     * @param o Zu schreibender Wert.
     * @param row Zu speichernde Row-Koordinate der Tabelle.
     * @param col Zu speicherne Col-Koordinate der Tabelle.
     */
    public void setValueAt(Object[] o, int row, int col) {
        data[row][col] = o;
    }

    /**
     * Liefert den kompletten Inhalt der Zelle.
     * @param row             the row whose value is to be queried
     * @param col          the column whose value is to be queried
     * @return Kompletter Inhalt der Zelle.
     */
    public Object[] getCompleteValueAt(int row, int col) {
        return (Object[]) data[row][col];
    }

    /**
     * Spalteninhalt soll als Icon ausgegeben werden.
     * @param col  the column being queried
     * @return
     */
    @Override
    public Class getColumnClass(int col) {
        return Icon.class;
    }

    /**
     * Setzt einen Wert in einen Zellenbereich.
     * @param data Zu setzender Wert.
     * @param rows Zu ueberschreibende Row-Koordinaten.
     * @param cols Zu uebschreibende Col-Koordinaten.
     */
    public void setValueAtRange(Object[] data, int[] rows, int[] cols) {
        for(int row = 0; row < rows.length; row++) {
            for(int col = 0; col < cols.length; col++) {
                setValueAt(data, rows[row], cols[col]);
            }
        }
    }

    /**
     * Holt alle gespeicherten Werte der Tabelle.
     * @return Alle gespeicherten Werte.
     */
    public Object[][] getData() {
        Object[][] ret = new Object[tileController.maxBildschirmZeilen][tileController.maxBildschirmSpalten];
        for(int i = 0; i < tileController.maxBildschirmZeilen; i++) {
            for(int j = 0; j < tileController.maxBildschirmSpalten; j++) {
                ret[i][j] = data[i][j];
            }
        }
        return ret;
    }

    /**
     * Uberschreibt alle Werte der Tabelle
     * @param data Zu setzende Daten.
     */
    public void setData(Object[][] data) {
        this.data = data;
    }
}
