package com.mapeditor.app;


import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Panel, die ein Bild und die zugehoerige ID besitzt.
 */
public class PaintPanel extends JPanel implements Transferable, DragSourceListener, DragGestureListener{
    /**
     * Dazustellendes Bild.
     */
    BufferedImage img;
    /**
     * Korrespondierende ID.
     */
    int id;

    /**
     * DragSource fuer Drag'n'Drop-Feature.
     */
    private DragSource source;
    /**
     * TransferHandler fuer Drag'n'Drop-Feature.
     */
    private  TransferHandler t;

    /**
     * Erzeugt Instanz. Setzt ID und Bild.
     *
     * @param id  Zu setzende ID.
     * @param img Zu setzendes Bild.
     */
    public PaintPanel(int id, BufferedImage img) {
        super(true);
        this.img = img;
        this.id = id;
        this.setPreferredSize(new Dimension(img.getWidth() * 2, img.getHeight() * 2));

        t = new TransferHandler() {
            public Transferable createTransferable(JComponent c){
                return new PaintPanel(id, img);
            }
        };
        setTransferHandler(t);
        source = new DragSource();
        source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }

    /**
     * Bild wird angezeigt.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 20, 10, img.getWidth() * 2, img.getHeight() * 2, this);
    }

    /**
     * Tile wird als JPanel bevorzugt rueckgegeben.
     * @return
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { new DataFlavor(PaintPanel.class, "JPanel")};
    }

    /**
     * Prueft, ob DataFlovor, der eingegeben wird, unterstuetzt wird.
     * @param flavor the requested flavor for the data
     * @return Immer true. Alles wird akzeptiert.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    /**
     * Holt die aktuellen zu transferierenden Daten.
     * @param flavor the requested flavor for the data.
     * @return Die Transfer-Daten als neues PaintPanel.
     */
    @Override
    public Object getTransferData(DataFlavor flavor) {
        return new PaintPanel(id, img);
    }

    public void dragDropEnd(DragSourceDragEvent dsde) {
        repaint();
    }

    /**
     * Startet den Drag'n'Drop-Vorgang.
     * @param dge the {@code DragGestureEvent} describing
     * the gesture that has just occurred
     */
    public void dragGestureRecognized(DragGestureEvent dge) {
        source.startDrag(dge, DragSource.DefaultCopyDrop, new PaintPanel(id, img), this);
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {

    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragExit(DragSourceEvent dse) {

    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {

    }
}
