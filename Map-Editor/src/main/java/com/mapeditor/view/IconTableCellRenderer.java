package com.mapeditor.view;


import com.mapeditor.controller.TileController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Bilder werden neu skaliert, ausgewaehlte Bereiche werden hervorgehoben und
 * Check- und Startpunkte werden grafisch dargestellt.
 */
class IconTableCellRenderer extends DefaultTableCellRenderer {

    /**
     * Das eingegebene Bild wird um den angegebenen Scale-Faktor und Offset ueberarbeitet.
     * @param imageIcon Das zu ueberarbeitende Bild.
     * @param scaleFaktor Der neue Skalierfaktor.
     * @param offset Der neue Offset.
     * @return Ueberarbeitetes Bild.
     */
    public BufferedImage rescaleImage(ImageIcon imageIcon, float scaleFaktor, int offset) {
        RescaleOp rescaleOp = new RescaleOp(scaleFaktor, offset, null);
        Image img = imageIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return rescaleOp.filter(bufferedImage, null);
    }

    /**
     * Falls ein Bereich vom Anwender markiert ist, so wird dieser heller dargestellt.
     * Falls Check- oder Startpunkte gesetzt worden sind, so werden diese auch grafisch dargestellt.
     * Andernfalls werden die Bilder normal in den Zellen dargestellt.
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *                  <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param col the column of the cell to render
     * @return Die Komponente der Render-Komponente.
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);
        TileTableModel model = (TileTableModel) table.getModel();

        Object[] valueAt = model.getCompleteValueAt(row, col);

        //Heller machen
        if (isSelected) {
            setIcon(new ImageIcon(rescaleImage((ImageIcon) valueAt[1], 1.2f, 15)));
        }
        //Original behalten
        else {
            setIcon((ImageIcon) valueAt[1]);
        }

        String checks = TileController.getInstance().getCheckpunkte();
        if(checks != "") {
            String[] checkpoints = checks.split("\n");
            for (int i = 0; i < checkpoints.length; i++) {
                String[] koordinaten = checkpoints[i].split(" ");
                int checkRow = Integer.parseInt(koordinaten[1]) - 1;
                int checkCol = Integer.parseInt(koordinaten[0]) - 1;

                if (checkRow == row && checkCol == col) {
                    setIcon(new ImageIcon(rescaleImage((ImageIcon) valueAt[1], 1.5f, 10)));
                }
            }
        }

        String starts = TileController.getInstance().getStartposition();
        if(starts != "") {
            String[] startpoints = starts.split("\n");
            for (int i = 0; i < startpoints.length; i++) {
                String[] koordinaten = startpoints[i].split(" ");
                int checkRow = Integer.parseInt(koordinaten[1]);
                int checkCol = Integer.parseInt(koordinaten[0]);

                if (checkRow == row && checkCol == col) {
                    setIcon(new ImageIcon(rescaleImage((ImageIcon) valueAt[1], 0.7f, 10)));
                }
            }
        }


        return c;
    }
}
