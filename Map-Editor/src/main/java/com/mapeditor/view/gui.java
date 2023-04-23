package com.mapeditor.view;

import com.mapeditor.controller.*;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Erstellt die Ausgabe-GUI.
 */
public class gui extends JFrame {//inheriting JFrame
    /**
     * Die Orginigal-Groesse der Tiles.
     */
    public int tileOrginalGroessen = 16; //16x16 Tile
    /**
     * Die Vergroesserung, mit der die Tiles dargestellt werden sollen.
     */
    public int vergroesserung = 3;

    public int tileGroesse = tileOrginalGroessen * vergroesserung;
    public int maxBildschirmSpalten = 30;
    public int maxBildschirmZeilen = 20;

    /**
     * Instanz des TileControllers, der als Ansprechpartner zur Logik-Schicht fungiert.
     */
    public TileController tileController;
    /**
     * Aktueller Zustand des Spielfeldes. Fuer die Memento-Instanzen.
     */
    Originator originator;
    /**
     * Beinhaltet moegliche Aktionen, die bspw. bei Knopfdruck passieren koennen.
     */
    ActionCollection actionCollection;
    /***
     * Loggt die Ausgaben.
     */
    Logger logger = Logger.getLogger("mapeditor");

    /**
     * Erschafft das Hauptfenster. Hierbei ist die Tile-Auswahl, die Tabelle zur Spielfeld-Erstellung und die moeglichen
     * Aktionen (als Buttons und Menue-Punkte) miteingeschlossen.
     * @param titlebar Den Namen des Hauptfensters.
     */
    public void erschaffeFenster(String titlebar) {
        //UIManager uiManager = new UIManager();

        tileController = TileController.getInstance();

        setTitle(titlebar);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //Menue mit den verfuegbaren Tiles
        JPanel panelMenu = erstelleTileauswahl();
        JScrollPane jsp = new JScrollPane(panelMenu);
        jsp.getVerticalScrollBar().setUnitIncrement(16);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        add(jsp, gbc);

        //Tabelle
        JPanel panel1 = new JPanel(new BorderLayout());
        JTable table = erstelleTable();
        panel1.add(new JScrollPane(table));
        gbc.gridx = 1;
        gbc.weightx = 1;
        add(panel1, gbc);

        actionCollection = new ActionCollection(table, originator);

        JPanel tasks = erstelleButtonleiste();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.2;
        add(tasks, gbc);

        JMenuBar menubar = erstelleMenu(table);
        setJMenuBar(menubar);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Erstellt das Menue, mit den dazugehörigen Aktionen.
     * @param table Die zu ueberwachende Tabelle.
     * @return Die gefuellte und einsatzbereite Menue-Bar.
     */
    private JMenuBar erstelleMenu(JTable table) {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Optionen");

        //Button, der das Konvertieren startet
        JMenuItem speichern = erstelleSpeicherPunkt();
        menu.add(speichern);

        //Button, um Map von Disk einzulesen
        JMenuItem browser = fileVonExplorer();
        menu.add(browser);
        menu.addSeparator();

        //Button, der Kartengroesse aendert
        JMenuItem kartengroesse = setzteKartenGroesse();
        menu.add(kartengroesse);

        //Button, der markierte Bereiche als Startpunkte setzt.
        JMenuItem startpunkte = setzeStartpunkte();
        menu.add(startpunkte);

        //Button, der markierte Bereiche als Checkpunkte setzt.
        JMenuItem checkpunkte = setzeCheckpunkte();
        menu.add(checkpunkte);

        //Button, um freie Felder grün zu machen
        JMenuItem fuelle = erstelleGreenPunkt();
        menu.add(fuelle);
        menu.addSeparator();

        //Button, um erstelle Checkpunkte zu loeschen
        JMenuItem deleteCheck = deleteCheckpunkteAusMenu();
        menu.add(deleteCheck);

        //Button, um erstellte Startpunkte zu loeschen
        JMenuItem deleteStart = deleteStartAusMenu();
        menu.add(deleteStart);

        menu.addSeparator();

        //Button, um letzte Aktion rueckgaenig zu machen
        JMenuItem undo = undoAusMenu();
        menu.add(undo);

        //Button, um eine Aktion nach vorne zu springen
        JMenuItem restore = restoreAusMenu();
        menu.add(restore);

        //Button, um das Program aus dem Menue heraus zu beenden
        JMenuItem beenden = beendenAusMenu();
        menu.add(beenden);

        menubar.add(menu);
        return menubar;
    }


    /**
     * Setzt Hotkeys im Schema Ctrl + acc.
     * @param mi Das Menue-Item, das einen Hotkey haben soll.
     * @param acc Taste, die als Hotkey fungieren soll.
     */
    private void setCtrlAccelerator(JMenuItem mi, char acc) {
        KeyStroke ks = KeyStroke.getKeyStroke(
                acc, Event.CTRL_MASK
        );
        mi.setAccelerator(ks);
    }

    /**
     * Erstellt die Tabelle mit Default-Rand und Drag'n'Drop-Kompatibilitaet.
     * Spalten-Nummern werden gesetzt.
     * @return Die erstellte Tabelle mit Default-Rand und Drag'n'Drop-Kompatibilitaet.
     */
    public JTable erstelleTable() {
        tileController = TileController.getInstance();
        Object[][] data = new Object[tileController.maxBildschirmZeilen][tileController.maxBildschirmSpalten];
        HashMap<Integer, BufferedImage> spielfeldTiles = tileController.spielfeldTiles;

        //Leeres Spielfeld
        ImageIcon icon = TableConverter.resizeImageIcon(spielfeldTiles.get(-1));

        //Tabelle mit Default-Bild initialisieren
        Object[] leer = new Object[]{
                tileController.defaultTile, icon
        };
        for (int i = 0; i < tileController.maxBildschirmZeilen; i++) {
            Arrays.fill(data[i], leer);
        }

        //Grenzen des Spielfeldes erstellen
        icon = TableConverter.resizeImageIcon(spielfeldTiles.get(10));
        Object[] grenze = new Object[]{
                10, icon
        };
        for (int i = 0; i < tileController.maxBildschirmSpalten; i++) {
            data[0][i] = grenze;
            data[tileController.maxBildschirmZeilen - 1][i] = grenze;
        }
        for (int i = 0; i < tileController.maxBildschirmZeilen; i++) {
            data[i][0] = grenze;
            data[i][tileController.maxBildschirmSpalten -1] = grenze;
        }


        //Spalten-Namen erstellen
        String[] columnNames = new String[tileController.maxBildschirmSpalten];
        for (int i = 1; i <= tileController.maxBildschirmSpalten; i++) {
            columnNames[i - 1] = Integer.toString(i);
        }

        //Tabelle erzeugen
        TileTableModel model = new TileTableModel(data, columnNames);
        JTable j = new JTable(model);

        j.setPreferredScrollableViewportSize(j.getPreferredSize());
        j.setRowHeight(16 * 2);
        TableColumnModel columnModel = j.getColumnModel();
        for (int i = 0; i < tileController.maxBildschirmSpalten; i++) {
            columnModel.getColumn(i).setMaxWidth(16 * 2);
        }

        j.setDragEnabled(true);
        j.setDropMode(DropMode.ON);
        j.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        j.setColumnSelectionAllowed(true);
        j.setRowSelectionAllowed(true);

        // Fuer Drag'n'Drop-Kompatibilitate
        TransferHandler tableTransfer = new TransferHandler() {
            @Override
            public boolean canImport(JComponent comp,
                                     DataFlavor[] transferFlavors) {
                return true;
            }

            @Override
            public boolean importData(JComponent c, Transferable t) {
                if (canImport(c, t.getTransferDataFlavors())) {
                    try {
                        logger.fine("Drag & Drop in Tabelle");
                        JTable table = (JTable) c;
                        PaintPanel p = (PaintPanel) t.getTransferData(DataFlavor.javaFileListFlavor);

                        ImageIcon image = TableConverter.resizeImageIcon(p.img);
                        Object[] newData = new Object[]{p.id, image};

                        TileTableModel model = (TileTableModel) table.getModel();

                        int[] rows = table.getSelectedRows();

                        if (rows.length > 0) {
                            int[] cols = table.getSelectedColumns();
                            rows = Arrays.stream(rows).filter(e -> !(e == 0 || e == tileController.maxBildschirmZeilen - 1)).toArray();
                            cols = Arrays.stream(cols).filter(e -> !(e == 0 || e == tileController.maxBildschirmSpalten - 1)).toArray();
                            model.setValueAtRange(newData, rows, cols);
                        } else {
                            int row = table.getDropLocation().getRow();
                            int col = table.getDropLocation().getColumn();
                            if(row == 0 || row == tileController.maxBildschirmZeilen - 1 || col == 0 || col == tileController.maxBildschirmSpalten - 1) {
                                logger.warning("Der Rand darf nicht ueberschrieben werden!");
                                return false;
                            }
                            else {
                                model.setValueAt(newData, row, col);
                            }
                        }
                        model.fireTableDataChanged();

                        originator.setData(model.getData());
                        originator.createSavepoint();

                    } catch (UnsupportedFlavorException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }
        };
        j.setTransferHandler(tableTransfer);
        j.setDefaultRenderer(Icon.class, new IconTableCellRenderer());
        //Neue Instanz des Originators erstellen, da jetzt das Program erst losgeht
        originator = new Originator(model.getData(), tileController.getStartposition(), tileController.getCheckpunkte(), new Caretaker());
        TableConverter.originator = originator;

        return j;
    }

    /**
     * Erstellt die Buttonleiste, mit moeglichen Aktonen.
     * Beinhaltet selbe Aktionen, die im Menue zu finden sind.
     * @return Panel, dass die Buttonleiste beinhaltet.
     */
    public JPanel erstelleButtonleiste() {
        JPanel panel = new JPanel(new GridLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.WEST;
        gbc.weightx = 0.4;
        gbc.weighty = 0.4;

        //Speichern
        JPanel sizePanel = new JPanel();
        JButton speicherButton = new JButton(actionCollection.allesSpeichern);
        speicherButton.setText("Speichern");
        sizePanel.add(speicherButton);
        //speicherButton.setSize(10, 10);
        panel.add(sizePanel);

        //Datei oeffnen
        sizePanel = new JPanel();
        JButton dateiButton = new JButton(actionCollection.fuelleTableMitFile);
        dateiButton.setText("Datei öffnen");
        sizePanel.add(dateiButton);
        panel.add(sizePanel);

        //Startpunkt setzen
        sizePanel = new JPanel();
        JButton startpktButton = new JButton(actionCollection.setzeStartpunkte);
        startpktButton.setText("Setze Startpunkte");
        sizePanel.add(startpktButton);
        panel.add(sizePanel);

        //Checkpunkt setzen
        sizePanel = new JPanel();
        JButton checkpktButton = new JButton(actionCollection.setzeCheckpunkte);
        checkpktButton.setText("Setze Checkpunkte");
        sizePanel.add(checkpktButton);
        panel.add(sizePanel);

        //Rest mit Grass fuellen
        sizePanel = new JPanel();
        JButton restButton = new JButton(actionCollection.fuelleRest);
        restButton.setText("Fülle Rest");
        sizePanel.add(restButton);
        panel.add(sizePanel);

        //Letzte Aktion rueckgaengig machen
        sizePanel = new JPanel();
        JButton undoButton = new JButton(actionCollection.undo);
        undoButton.setText("Rückgängig");
        sizePanel.add(undoButton);
        panel.add(sizePanel);

        return panel;
    }

    /**
     * Erstellt die Tile-Auswahl mit allen moeglichen Tiles, die beim Einlesen gefunden wurden.
     * @return Panel, das die Tile-Auswahl beinhaltet.
     */
    public JPanel erstelleTileauswahl() {
        //Menue mit Tile-Bildern
        JPanel panel = new JPanel(new GridLayout(20, 3));

        //Alle Tilebilder laden
        for (Map.Entry<Integer, BufferedImage> entry : tileController.spielfeldTiles.entrySet()) {
            if (entry.getKey() == tileController.defaultTile) {
                continue;
            }
            PaintPanel paintPanel = new PaintPanel(entry.getKey(), entry.getValue());
            //paintPanel.setTransferHandler(new TransferHandler("text"));
            paintPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    PaintPanel c = (PaintPanel) e.getSource();
                    TransferHandler handler = c.getTransferHandler();
                    //c.getTransferHandler().exportAsDrag(c, e, TransferHandler.COPY);
                    handler.exportAsDrag(c, e, TransferHandler.COPY);
                }
            });
            panel.add(paintPanel);
        }

        return panel;
    }

    /**
     * Erstellt den Speicher-Menue-Punkt.
     * Hot-Key: Ctrl+s
     * @return Speicher-Menue-Item
     */
    public JMenuItem erstelleSpeicherPunkt() {
        JMenuItem item = new JMenuItem(actionCollection.allesSpeichern);
        setCtrlAccelerator(item, 'S');
        item.setText("Speichern");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der leere Felder der Tabelle mit Grass-Tiles fuellt.
     * Hot-Key: Ctrl+r
     * @return Grass-Menue-Item.
     */
    public JMenuItem erstelleGreenPunkt() {
        JMenuItem item = new JMenuItem(actionCollection.fuelleRest);
        setCtrlAccelerator(item, 'R');
        item.setText("Fülle Rest");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der ueber den File-Explorer ein bereits erstelltes Spielfeld einliest.
     * Hot-Key Ctrl+d
     * @return Speicher-Menue-Item.
     */
    public JMenuItem fileVonExplorer() {
        JMenuItem item = new JMenuItem(actionCollection.fuelleTableMitFile);
        setCtrlAccelerator(item, 'D');
        item.setText("Datei öffnen");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der den aktuell markierten Bereich als Startpunkt setzt.
     * Hot-Key: Ctrl+p
     * @return
     */
    public JMenuItem setzeStartpunkte() {
        JMenuItem item = new JMenuItem(actionCollection.setzeStartpunkte);
        setCtrlAccelerator(item, 'P');
        item.setText("Setze Startpunkte");

        return item;
    }

    /**
     *
     */
    public JMenuItem setzteKartenGroesse(){
        JMenuItem item = new JMenuItem(actionCollection.aendereKartengroesse );
        item.setText("Ändere Kartengroesse");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der den aktuell markierten Bereich als Checkpunkt setzt.
     * Hot-Key Ctrl+c
     * @return
     */
    public JMenuItem setzeCheckpunkte() {
        JMenuItem item = new JMenuItem(actionCollection.setzeCheckpunkte);
        setCtrlAccelerator(item, 'C');
        item.setText("Setze Checkpunkte");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der eine Aktion rueckgaengig macht.
     * Hot-Key Ctrl+z
     * @return
     */
    JMenuItem undoAusMenu() {
        JMenuItem item = new JMenuItem(actionCollection.undo);
        setCtrlAccelerator(item, 'Z');
        item.setText("Rückgänging");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der eine Aktion nach vorne springt.
     * Hot-Key: Ctrl+y
     * @return
     */
    JMenuItem restoreAusMenu() {
        JMenuItem item = new JMenuItem(actionCollection.restore);
        setCtrlAccelerator(item, 'Y');
        item.setText("Wiederherstellen");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der das Programm beendet.
     * Hot-Key Ctrl+x
     * @return
     */
    JMenuItem beendenAusMenu() {
        JMenuItem item = new JMenuItem(actionCollection.exit);
        setCtrlAccelerator(item, 'X');
        item.setText("Beenden");

        return item;
    }

    /**
     * Erstellt Menue-Punkt, der alle gesetzten Startpunkte loescht.
     * @return
     */
    JMenuItem deleteStartAusMenu() {
        JMenuItem item = new JMenuItem(actionCollection.deleteStartpunkte);
        item.setText("Alle Startpositionen löschen");

        return item;
    }


    /**
     * Erstellt Menue-Punkt, der alls gesetzten Checkpunkte loescht.
     * @return
     */
    JMenuItem deleteCheckpunkteAusMenu() {
        JMenuItem item = new JMenuItem(actionCollection.deleteCheckpunkte);
        item.setText("Alle Checkpunkte löschen");

        return item;
    }


}