package spielansichtsschicht;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;
import kiansichtsschicht.SpielKnoten;

/**
 * Klasse die alle grafischen Spielobjekte verwaltet, sowie zeichnet
 * @author André
 *
 */
public class SpielObjekteManager implements EventListener  {
	EventManager event;
	Spieloptionen optionen;
	
	public Controller controller;
	/**
	 * Eine Hash-Map die alle grafischen Spielobjekte beinhaltet, Schluessel, SpielerID, Wert = Entity
	 */
	public Map<Integer, Entity> entityList = null;
	
	/**
	 * Anmelden beim Ereignismanager ueber eingehende Ereignisse
	 */
	public SpielObjekteManager(){
		optionen = Spieloptionen.getInstance();
		event = EventManager.getInstance();
		event.subscribe("update_koordinate", this); //als EventManager identifizieren
		event.subscribe("add_view_car", this);
	}
	
	public void addController(Controller controller) {
		this.controller = controller;
		
	}
	
	/**
	 * In der Hash-Map eine neues Spielobjekt hinzufuegen
	 * @param id SpielerID
	 * @param entity Spielobjekt
	 */
	public void addEntity(Integer id, Entity entity) {
		if(entityList == null) {
			entityList = new HashMap<Integer, Entity>();
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
	 * Zeichne alle Spielobjekte auf das JPanel
	 * @param g2 Graphics2D
	 */
	public void drawEntities(Graphics2D g2) { //Zeichnen jedes Element in der Entitiy List
		if(entityList != null) {
		entityList.forEach((key,value)->{
			//System.out.println(key+" = "+value); //Debug
			value.draw(g2);
		});
		}
	}

	@Override
	/**
	 * Empfange eingehende Ereignisse und verarbeite sie weiter
	 */
	public void updateEvent(String eventType, Object... eventData) {
		
		if(entityList != null && eventType =="update_koordinate" ) {
			
			for (Map.Entry<Integer, Entity> entry : entityList.entrySet()) {
				if(entry.getKey() == eventData[0]) {
					entry.getValue().direction = (String) eventData[1];
					entry.getValue().worldX = (int) eventData[2];
					entry.getValue().worldY = (int) eventData[3];
					}
			}
		}

		if(eventType == "add_view_car") {
			
			String autotyp = (String)eventData[1];
			
			//Fabrik "make Car string iD
			if("RotesAuto" == (String)eventData[1] || autotyp.equals("RotesAuto") ) {
				RotesAuto rauto = new RotesAuto(controller);
				rauto.worldX = (int) ((double) eventData[2]) * optionen.tileGroesse ;
				rauto.worldY = (int) ((double) eventData[3])* optionen.tileGroesse ;
				rauto.direction = (String) eventData[4];
				addEntity((int)eventData[0], rauto);
				
			}
			if("BlauesAuto" == (String)eventData[1] || autotyp.equals("BlauesAuto") ) {
				BlauesAuto bauto = new BlauesAuto(controller);
				bauto.worldX = (int) ((double) eventData[2]) * optionen.tileGroesse ;
				bauto.worldY = (int) ((double) eventData[3])* optionen.tileGroesse ;
				bauto.direction = (String) eventData[4];
				addEntity((int)eventData[0], bauto);
			
		}
			if("GruenesAuto" == (String)eventData[1] || autotyp.equals("GruenesAuto") ) {
				GruenesAuto gauto = new GruenesAuto(controller);
				gauto.worldX = (int) ((double) eventData[2]) * optionen.tileGroesse ;
				gauto.worldY = (int) ((double) eventData[3])* optionen.tileGroesse ;
				gauto.direction = (String) eventData[4];
				addEntity((int)eventData[0], gauto);
				
			}
			if("GelbesAuto" == (String)eventData[1] || autotyp.equals("GelbesAuto") ) {
				GelbesAuto gauto = new GelbesAuto(controller);
				gauto.worldX = (int) ((double) eventData[2]) * optionen.tileGroesse ;
				gauto.worldY = (int) ((double) eventData[3])* optionen.tileGroesse ;
				gauto.direction = (String) eventData[4];
				addEntity((int)eventData[0], gauto);
				
			}

		}

	}
}
