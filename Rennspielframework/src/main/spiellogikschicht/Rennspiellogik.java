package spiellogikschicht;

import anwendungsschicht.EventManager;

/**
 * Spiellogik fuer ein Rennspiel
 * @author André
 *
 */
public class Rennspiellogik extends BasicSpiellogik{
	EventManager event;
	
	/**
	 * Konstruktor
	 */
	public Rennspiellogik(){
		super();
		event = EventManager.getInstance();
		event.subscribe("add_spielakteur", this);
	}
	
	public Rennspiellogik(String str) {
		super(str);
	}
	
	/**
	 * Intialisierungsfunktion der Rennspiellogik
	 */
	public boolean Init() {
		super.Init();
		return true;
	}

	
	@Override
	/**
	 * Registriere Ereigniss die fuer die Spiel-Logik bestimmt sind
	 */
	public void updateEvent(String eventType, Object... eventData) {
		if(eventType == "add_spielakteur") {
			int spielerID = (int) eventData[0];
			int autoID = (int) eventData[1];
			
			fuegeSpielakteurHinzu(spielerID, autoID);
		}
		
	}

}
