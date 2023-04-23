package gui_opengl;

import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasse die Spielobjekte verwaltet
 */
public class SpielobjekteManager implements EventListener {

        EventManager event;
        Spieloptionen optionen;

        //public Controller controller;
        /**
         * Eine Hash-Map die alle grafischen Spielobjekte beinhaltet, Schluessel, SpielerID, Wert = Entity
         */
        public Map<Integer, GameObject> entityList = null;

        /**
         * Anmelden beim Ereignismanager ueber eingehende Ereignisse
         */
        public SpielobjekteManager(){
            optionen = Spieloptionen.getInstance();
            event = EventManager.getInstance();
            event.subscribe("update_koordinate", this); //als EventManager identifizieren
            event.subscribe("add_view_car", this);
        }

        /*public void addController(Controller controller) {
            this.controller = controller;

        }*/

        /**
         * In der Hash-Map eine neues Spielobjekt hinzufuegen
         * @param id SpielerID
         * @param entity Spielobjekt
         */
        public void addEntity(Integer id, GameObject entity) {
            if(entityList == null) {
                entityList = new HashMap<Integer, GameObject>();
            }
            entityList.put(id, entity);

        }

        /**
         * Loesche aus der Liste ein Spielobjekt
         * @param id SpielerID
         */
        public void removeEntity(Integer id) { //Entfernt ein Spielobjekt aus der Liste
            entityList.remove(id);
        }

        /**
         * //Zeichnen jedes Element in der Entitiy List
         * @param renderer SpriteRenderer
         */
        public void Draw(SpriteRenderer renderer){
            if(entityList != null) {
                for (Map.Entry<Integer, GameObject> entry : entityList.entrySet()) {
                    entry.getValue().Draw(renderer);
                }
            }
            //Fuer den Menschlichen Spieler das neue Spieler die bestenden Spielerliste bekommen
            if(optionen.addClientsInformation != null && optionen.client ){
                for(Map.Entry<Integer, Object[]> entry : optionen.addClientsInformation.entrySet()){
                    Object[] eventData = entry.getValue();
                    if(true){
                        String autotyp = (String)eventData[0];

                        //Fabrik "make Car string iD
                        if("RotesAuto" == (String)eventData[0] || autotyp.equals("RotesAuto") ) {
                            RotesAuto rauto = new RotesAuto();
                            rauto.Position.x =   (int) ((double) eventData[1]) * optionen.tileGroesse ;
                            rauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[2])* optionen.tileGroesse ;
                            rauto.direction = (String) eventData[3];
                            addEntity((int)entry.getKey(), rauto);

                        }
                        if("BlauesAuto" == (String)eventData[0] || autotyp.equals("BlauesAuto") ) {
                            BlauesAuto bauto = new BlauesAuto();
                            bauto.Position.x = (int) ((double) eventData[1]) * optionen.tileGroesse ;
                            bauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[2])* optionen.tileGroesse ;
                            bauto.direction = (String) eventData[3];
                            addEntity((int)entry.getKey(), bauto);

                        }
                        if("GruenesAuto" == (String)eventData[0] || autotyp.equals("GruenesAuto") ) {
                            GruenesAuto gauto = new GruenesAuto();
                            gauto.Position.x = (int) ((double) eventData[1]) * optionen.tileGroesse ;
                            gauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[2])* optionen.tileGroesse ;
                            gauto.direction = (String) eventData[3];
                            addEntity((int)entry.getKey(), gauto);

                        }
                        if("GelbesAuto" == (String)eventData[0] || autotyp.equals("GelbesAuto") ) {
                            GelbesAuto gauto = new GelbesAuto();
                            gauto.Position.x = (int) ((double) eventData[1]) * optionen.tileGroesse ;
                            gauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[2])* optionen.tileGroesse ;
                            gauto.direction = (String) eventData[3];
                            addEntity((int)entry.getKey(), gauto);

                        }
                    }

                    optionen.addClientsInformation.remove(entry.getKey());
                }
            }
        }

        @Override
        /**
         * Empfange eingehende Ereignisse und verarbeite sie weiter
         */
        public void updateEvent(String eventType, Object... eventData) {

            if(entityList != null && eventType =="update_koordinate" ) {

                for (Map.Entry<Integer, GameObject> entry : entityList.entrySet()) {
                    if(entry.getKey() == eventData[0]) {
                        entry.getValue().direction = (String) eventData[1];
                        entry.getValue().Position.x =  (int) eventData[2];
                        entry.getValue().Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) eventData[3];
                    }
                }
            }

            if(eventType == "add_view_car") {

                String autotyp = (String)eventData[1];

                //Fabrik "make Car string iD
                if("RotesAuto" == (String)eventData[1] || autotyp.equals("RotesAuto") ) {
                    RotesAuto rauto = new RotesAuto();
                    rauto.Position.x =   (int) ((double) eventData[2]) * optionen.tileGroesse ;
                    rauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[3])* optionen.tileGroesse ;
                    rauto.direction = (String) eventData[4];
                    addEntity((int)eventData[0], rauto);

                }
                if("BlauesAuto" == (String)eventData[1] || autotyp.equals("BlauesAuto") ) {
                    BlauesAuto bauto = new BlauesAuto();
                    bauto.Position.x = (int) ((double) eventData[2]) * optionen.tileGroesse ;
                    bauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[3])* optionen.tileGroesse ;
                    bauto.direction = (String) eventData[4];
                    addEntity((int)eventData[0], bauto);

                }
                if("GruenesAuto" == (String)eventData[1] || autotyp.equals("GruenesAuto") ) {
                    GruenesAuto gauto = new GruenesAuto();
                    gauto.Position.x = (int) ((double) eventData[2]) * optionen.tileGroesse ;
                    gauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[3])* optionen.tileGroesse ;
                    gauto.direction = (String) eventData[4];
                    addEntity((int)eventData[0], gauto);

                }
                if("GelbesAuto" == (String)eventData[1] || autotyp.equals("GelbesAuto") ) {
                    GelbesAuto gauto = new GelbesAuto();
                    gauto.Position.x = (int) ((double) eventData[2]) * optionen.tileGroesse ;
                    gauto.Position.y = ((optionen.tileGroesse * optionen.maxBildschirmZeilen) - optionen.tileGroesse) - (int) ((double) eventData[3])* optionen.tileGroesse ;
                    gauto.direction = (String) eventData[4];
                    addEntity((int)eventData[0], gauto);

                }

            }

        }
    }

