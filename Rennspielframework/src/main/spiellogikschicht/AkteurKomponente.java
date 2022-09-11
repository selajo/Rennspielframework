package spiellogikschicht;
/**
 * 
 * @author André
 *	Abstrakte Klasse von der alle Komponenten abstammen
 */
public abstract class AkteurKomponente {
	/**
	 * Enthaelt den Spielakteur zu dem die Komponente gehoert
	 */
	protected Spielakteur akteur; //Instanz zu dem Spielobjekt dem die Komponente zugeordnet ist.
	
	/**
	 * Update alle Kompoenenteneigenschaften
	 */
	public abstract void Update();			//Die Funktionen sollen vom individuellen Interface ueberschrieben werden
	
	/*
	 * Die Funktion soll von jedem Interface speziell ueberschieben werden und eine eindeutige ID bekommen
	 */
	public abstract int GetKomponentId(); //Die Funktion soll von jedem Interface speziell ueberschieben werden und eine eindeutige ID bekommen
	/**
	 * /Gibt den Namen der Komponente zurueck
	 * @return String KomponentenName
	 */
	public abstract String VGetName(); 			//Gibt den Namen der Komponente zurueck
	/**
	 * Wird Ueberschrieben von der entgueltigen Komponente
	 * @param name String
	 * @return int KomponentenID
	 */
	public abstract int getIdVonName(String name); //Ueberschreiben von der entgueltigen Komponente
	/**
	 * Bearbeitet eingegangende Ereignisupdates
	 * @param eventType Ereignistyp
	 * @param eventData	Ereingisdaten
	 */
	public abstract void updateKomponentenEvent(String eventType, Object... eventData);
	
	/**
	 * Legt den Spielakteur fest zu dem der Spieler gehoert
	 * @param akteur
	 */
	public void SetBesitzerSpielakteur (Spielakteur akteur) {this.akteur = akteur;}
}
