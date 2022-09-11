package com.mapeditor.app;

import com.mapeditor.controller.MapException;
import com.mapeditor.controller.Originator;
import com.mapeditor.controller.TableConverter;
import com.mapeditor.controller.TileController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Beinhaltet alle zu machenden Aktionen, die z.B. bei Knopfdruck erfolgen koennen.
 */
public class ActionCollection {
    /**
     * Zu behandelnde Tabelle.
     */
    JTable table;
    /**
     * Aktuelle TileController-Instanz.
     */
    TileController tileController = TileController.getInstance();
    /**
     * Loggt die Ausgabe.
     */
    Logger logger = Logger.getLogger("mapeditor");
    /**
     * Aktueller Zustand des Spielfeldes.
     */
    Originator originator;

    /**
     * Erzeugt Instanz von ActionCollection.
     *
     * @param table      Aktuelle Tabelle.
     * @param originator Aktueller Originator.
     */
    public ActionCollection(JTable table, Originator originator) {
        this.table = table;
        this.originator = originator;
    }

    /**
     * Programm wird mit Ret-Value 0 beendet.
     */
    public Action exit = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    /**
     * Speichert den markierten Bereich der Tabelle als Checkpunkte.
     */
    public Action setzeCheckpunkte = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] rows = table.getSelectedRows();
            int[] cols = table.getSelectedColumns();

            if (rows.length > 0) {
                tileController.addCheckpunkte(rows, cols);
                logger.info("Aktuelle Checkpunkte:\n" + tileController.getCheckpunkte());

                originator.setCheckpunkte(tileController.getCheckpunkte());
                originator.createSavepoint();
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Bitte Bereich markieren.");
            }
        }
    };

    /**
     * Speichert den markierten Bereich der Tabelle als Startpunkte.
     */
    public Action setzeStartpunkte = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] rows = table.getSelectedRows();
            int[] cols = table.getSelectedColumns();

            String[] values = {"oben", "unten", "links", "rechts"};
            Object selected = JOptionPane.showInputDialog(null,
                    "In welche Richtung sollen die Fahrzeuge zu Beginn fahren?", "Selection",
                    JOptionPane.DEFAULT_OPTION, null, values, "0");
            if (selected != null) {
                tileController.addStartpunkte(rows, cols, Arrays.asList(values).indexOf(selected) + 1);
                logger.info("Aktuelle Startpositionen:\n" + tileController.getStartposition());

                originator.setStartpunkte(tileController.getStartposition());
                originator.createSavepoint();
            } else {
                logger.config("Nutzer hat Vorgang abgebrochen");
            }
        }
    };

    /**
     * Der Anwender waehlt via GUI eine Datei aus.
     *
     * @return Der ausgewaehlte Datei-Pfad.
     */
    public String getFile() {
        String path = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            logger.info("Ausgewaehlte Datei: " + selectedFile.getAbsolutePath());
            path = selectedFile.getAbsolutePath();
        }
        return path;
    }

    /**
     * Die Tabelle wird via Konfigurationsdatei aus dem Speicher gefuellt.
     * Check- und Startpunkte werden hierbei beruecksichtigt.
     */
    public Action fuelleTableMitFile = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TileTableModel model = (TileTableModel) table.getModel();
            String file = getFile();
            if (!file.equals("")) {
                int[][] map = tileController.ladeMap(file);
                TableConverter.fuelleTabelleKomplett(model, map);
                originator.setData(model.getData());
                originator.setCheckpunkte(tileController.getCheckpunkte());
                originator.setStartpunkte(tileController.getStartposition());

                logger.info(originator.getData().toString());
                logger.info(tileController.getCheckpunkte());
                logger.info(tileController.getStartposition());

                originator.createSavepoint();
                model.fireTableDataChanged();
            } else {
                logger.config("Nutzer hat Vorgang abgebrochen.");
            }
        }
    };

    /**
     * Fuellt alle leeren Zellen der Tabelle mit Gras-Tiles.
     */
    public Action fuelleRest = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                TileTableModel model = (TileTableModel) table.getModel();
                TableConverter.fuelleRestMit(model, tileController.grassID);
                model.fireTableDataChanged();

                originator.setData(model.getData());
                originator.createSavepoint();
                logger.config(originator.dataToString());

            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), exception.getMessage());
            }
        }
    };

    /**
     * Speichert alle Informationen der Tabelle als Konfigurationsdatei ab.
     * Check- und Startpunkte werden hierbei beruecksichtigt.
     */
    public Action allesSpeichern = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog("Bitte den Namen dieses Spielfeldes eingeben (Ohne Dateiendung):");

            try {
                String map = TableConverter.convert(table);
                System.out.println(map);

                if (!TableConverter.errorMessage.equals("")) {
                    String errorMessage = "Folgende Fehler sind aufgetreten:\n" + TableConverter.errorMessage +
                            "Soll das aktuelle Spielfeld trotzdem gespeichert werden?";
                    int result = JOptionPane.showConfirmDialog(new JFrame(), errorMessage, "Informationen fehlen",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    TableConverter.errorMessage = "";
                    if (result == JOptionPane.NO_OPTION) {
                        logger.config("User hat abgelehnt.");
                        return;
                    } else if (result == JOptionPane.YES_OPTION) {
                        logger.info("Eingabe wird konvertiert...");
                        logger.info(map);
                        if (tileController.writeCompleteData(input, map)) {
                            JOptionPane.showMessageDialog(new JFrame(), "Dateien erfolgreich gespeichert.");
                        } else {
                            throw new Exception("Datei konnte nicht erstellt werden.");
                        }
                    } else {
                        logger.config("User hat nichts ausgewählt");
                        return;
                    }
                } else {
                    logger.info("Eingabe wird konvertiert...");
                    logger.info(map);
                    if (tileController.writeCompleteData(input, map)) {
                        JOptionPane.showMessageDialog(new JFrame(), "Dateien erfolgreich gespeichert.");
                    } else {
                        throw new Exception("Datei konnte nicht erstellt werden.");
                    }
                }

            } catch (MapException mapException) {
                JOptionPane.showMessageDialog(new JFrame(), "Alle notwendigen Informationen " +
                        "(Ziel, Startpositionen und Checkpoints) " +
                        "muessen angegeben sein.\n" +
                        "Fehler: " + mapException.getMessage());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), exception.getMessage());
            }
        }
    };

    /**
     * Macht die zuletzt gemachte Aktion rueckgaengig, falls dies moeglich ist.
     */
    public Action undo = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (originator.undo()) {
                logger.config(originator.dataToString());
                TileTableModel model = (TileTableModel) table.getModel();

                ladeDatenVonOriginator(model);
                model.fireTableDataChanged();
            } else {
                logger.info("Undo: Nicht mehr zulaessig");
            }
        }
    };

    /**
     * Geht eine Aktion nach vorne, falls dies moeglich ist.
     */
    public Action restore = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (originator.restore()) {
                logger.config(originator.dataToString());
                TileTableModel model = (TileTableModel) table.getModel();

                ladeDatenVonOriginator(model);
                model.fireTableDataChanged();
            } else {
                logger.info("Restore: Nicht mehr zulaessig");
            }
        }
    };

    /**
     * Leadt die akuell gespeicherten Daten des Originators in die Tabelle.
     * @param model Zu ueberschreibendes Model der Tabelle.
     */
    public void ladeDatenVonOriginator(TileTableModel model) {
        //Start- und Checkpunkte zuruecksetzen
        tileController.setStartposition(originator.getStartpunkte());
        tileController.setCheckpunkte(originator.getCheckpunkte());
        //Tabellendaten zuruecksetzen
        model.setData(originator.getData());
    }

    /**
     * Loescht alle gespeicherten Checkpunkte.
     */
    public Action deleteCheckpunkte = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Alle Checkpunkte loeschen");
            tileController.setCheckpunkte("");

            TileTableModel model = (TileTableModel) table.getModel();
            model.fireTableDataChanged();
        }
    };

    /**
     * Loescht alle gespeicherten Startpunkte.
     */
    public Action deleteStartpunkte = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Alle Startpunkte loeschen");
            tileController.setStartposition("");

            TileTableModel model = (TileTableModel) table.getModel();
            model.fireTableDataChanged();
        }
    };

}
