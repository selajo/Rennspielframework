package spiellogikschicht;

import java.util.HashMap;
import java.util.Map;

import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;


/**
 * 
 * @author André
 * Diese Klasse repraesentiert eine Instanz von einem speziellen Spielobjekt. Dabei kann jedes Spielobjekt indivinduell definiert sein.
 * Denn Diese Klasse ist nur ein Kontainer von verschieden Komponenten, die das Verhalten definieren.
 * 
 */
public class Spielakteur implements EventListener{
	
	EventManager event;
	/**
	 * Spielerakteur Postion auf dem Spielfeld
	 */
	public Vector2D position = new Vector2D(100,100); //Diefaultwert//Spielakteur Position
	/**
	 * Ausrichtung des Spielers auf dem Spielfeld
	 */
	public String direction = "down";
	/**
	 * HashMap mit allen Komponenten des Akteurs Schluessel: Komponententyp, Wert: AkteurKomponente
	 */
	public Map<Integer, AkteurKomponente> AkteurKomponenten = null;  //Eine Map die alle Komponenten eines Spielers beinhaltet.
	/**
	 * Die Id des Akteurs
	 */
	private int id; 			//Eindeutige ID eines Akteurs /unsigned int
	/**
	 * SpielakteurTyp
	 */
	private SpielakteurTyp type;		//Eindeutiger Typ des Akteurs
	/**
	 * AutoTyp als String
	 */
	public String resource; 					
	
	/**
	 * Initialisierung des Spielaktuers, Startposition und Startausrichtung wird festgelegt und zugewiesen zu welchem Spieler der Spielakteur gehoert
	 * Registierung beim EventManger fuer Ereignisse
	 * @param id SpielerID
	 * @param data SpielakteurDaten Object[]
	 */
	public Spielakteur(int id, Object... data) {
	Spieloptionen opt = Spieloptionen.getInstance();
	this.id = id;
	this.position.x = (double) data[0] * opt.tileGroesse; //Startpositionen gesetzt
	this.position.y = (double) data[1] * opt.tileGroesse; //Startpositionen gesetzt
	this.direction = (String) data[2];
	System.out.println("Actor " + id + " initialisert");
	event = EventManager.getInstance();
	event.subscribe("key_event", this);
	event.subscribe("add_collision_force", this);
	
	}; 	
	
	public int GetId() {return id;}
	public SpielakteurTyp GetType() {return type;} //Beispiel Rotes Auto
	
	/**
	 * Liefert die Komponete von einem bestimmten Typ zurueck
	 * @param komponentId
	 * @return
	 */
	public AkteurKomponente GetKomponente(int komponentId) {
			if(AkteurKomponenten != null) {
			
			for (Map.Entry<Integer, AkteurKomponente> entry : AkteurKomponenten.entrySet()) { //Update alle Komponenten
				if(entry.getValue().GetKomponentId()== komponentId) {
					return entry.getValue();
				};
		    }
		}
		return null;};
		
	public AkteurKomponente GetKomponente(String komponetName) {
			if(AkteurKomponenten != null) {
			
			for (Map.Entry<Integer, AkteurKomponente> entry : AkteurKomponenten.entrySet()) { //Update alle Komponenten
				if(entry.getValue().VGetName()== komponetName) {
					return entry.getValue();
				};
		    }
		}
		return null;};
	/**
	 * Fuege eine Komponente der Komponenen Hash-Map hinzu
	 * @param komponente AkteurKomponente
	 */
	public void AddKomponente(AkteurKomponente komponente) {
		if(AkteurKomponenten == null) {
			AkteurKomponenten = new HashMap<Integer, AkteurKomponente>();
		}
		int id = komponente.GetKomponentId();
		AkteurKomponenten.put(id, komponente);
	}
	
	/**
	 * Entferne eine Komponente aus der Komponenten Hash-Map
	 * @param komponentId
	 */
	public void removeKomponent(int komponentId) {
		if(AkteurKomponenten != null) {
			for (Map.Entry<Integer, AkteurKomponente> entry : AkteurKomponenten.entrySet()) { //Update alle Komponenten
				if(entry.getValue().GetKomponentId()== komponentId) {
					AkteurKomponenten.remove(komponentId);
				}
		    }
		}
	}
	
	/**
	 * Weiterleiten des Updateimpulses an die Komponenten
	 */
	public void update() {
		
		if(AkteurKomponenten != null) {
			
			for (Map.Entry<Integer, AkteurKomponente> entry : AkteurKomponenten.entrySet()) { //Update alle Komponenten
				entry.getValue().Update();
		    }
		}
	}
	
	
	@Override
	/**
	 * Eingehende Ereignisse wie TastaturEingaben des Spielers und Kollisionskraefte werden hier empfangen
	 */
	public void updateEvent(String eventType, Object... eventData) {
		if(eventType == "key_event") {
			if(AkteurKomponenten != null) {
			if((int)eventData[0]== id) {
			AkteurKomponenten.get(2).updateKomponentenEvent(eventType, eventData); //PhysikKomponente
			}
			}
		}
		if(eventType == "add_collision_force") {
			if(AkteurKomponenten != null) {
				if((int)eventData[0]== id) {
				AkteurKomponenten.get(2).updateKomponentenEvent(eventType, eventData); //PhysikKomponente
				}
			}
		}
			
	}
}
