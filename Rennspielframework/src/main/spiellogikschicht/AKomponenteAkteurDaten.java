package spiellogikschicht;

import anwendungsschicht.Spieloptionen;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Map;

/**
 * 
 * @author André
 *	Abstracte Klasse die alle Daten fuer einen Akteur Definiert
 */
public abstract class AKomponenteAkteurDaten extends AkteurKomponente{
	
	//Alle AkteurDatenKomponentenTypen besitzten die Geleichen IDs so das einem Spielobjekt nur eine DatenKomponente besitzen kann
	/**
	 * Die ID einer Datenkomponente ist 1
	 */
	public final int komponentId = 1;
	/**
	 * Kollsionshuelle die das Fahrzeug repaesentiert
	 */
	public Rectangle festeCollisionBox;
	/**
	 * Schnellste Rundenzeit in Millisekunden
	 */
	double besteRundenZeit = 0;//in Millisec
	/**
	 * Startzeit einer Runde in Millisekunden
	 */
	long rundenstart = 0;//in Millisec
	
	/**
	 * HashMap die alle Rundenzeiten eines Autos enthaelt
	 */
	public Map<Integer, Double> rundenZeiten = null; //Map die Integer Rundenzahl und Zeit speichert
	/**
	 * Die anzahl der vollstaendig gefahrenen Runden
	 */
	public int anzahlRunden = 0;					//Welche Runde sich das Auto momentan befindet
	/**
	 * Welchen Checkpoint das Fahrzeug schon ueberfahren hat
	 */
	public int checkpoints = 0; 						//Welcher Aktuelle Checkpoint sich das Auto momentan befindet //Event wenn Checkpoint uebersprungen wird
	/**
	 * Feststellen der Zeit beim ersten ueberfahren der Zielline
	 */
	public double timer;							//Initialisert bei erstem ueberfahren der Ziellinie	
	
	/**
	 * Intialisiere feste Daten wie Kollsionsbox
	 */
	public AKomponenteAkteurDaten() {
		festeCollisionBox = new Rectangle();
		festeCollisionBox.x = 10;
		festeCollisionBox.y = 10;
		festeCollisionBox.height = 28;
		festeCollisionBox.width = 28;
	}
	
	/**
	 * Ermittle die Rundenzeit und fuege eine Rundenzeit der Liste hinzu
	 * @param date aktuelle Zeit
	 */
	public void addRundenZeit(Date date) {
		Spieloptionen optionen = Spieloptionen.getInstance();
		if(besteRundenZeit == 0) {
			besteRundenZeit = date.getTime() - rundenstart; //in Millisec
			return;
		}
		if(date.getTime() - rundenstart < besteRundenZeit) {
			besteRundenZeit = date.getTime() - rundenstart;
		}

	}
	

	@Override
	public int GetKomponentId() {
		return komponentId;
	}
	
	@Override
	/**
	 * Moeglichkeit eingehende Ereignisse aufzufangen
	 */
	public void updateKomponentenEvent(String eventType, Object... eventData) {

	}


}
