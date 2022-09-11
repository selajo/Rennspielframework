package spiellogikschicht;


import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;


/**
 *  Diese Klasse erschafft und initialisiert Spielobjekte aus einer JSON-Datei
 * @author André
 */
public class AkteurFabrik {
	EventManager event = EventManager.getInstance();
	Spieloptionen opt = Spieloptionen.getInstance();
	
	public AkteurFabrik() {}
	
	/**
	 *Methode die einen Spielakteur erschafft. Benoetigt werden dafuer nur der Fahrzeugtyp und die Spieler ID
	 * @param spielerid SpielerID
	 * @param resource	String Autotyp
	 * @return	initialiserter Spielakteur
	 */
	public Spielakteur erschaffeAkteur(int spielerid, String resource) {
		int actorId = spielerid;
		Spielakteur akt = new Spielakteur(actorId, opt.startpositionenArray.get(actorId)); //Testcode	
		akt.resource = resource;	
		
		Object[] auto = opt.autoListe.get(resource);

			//Daten Komponente
			AkteurKomponente komponente = createKomponente((String)auto[1], auto);
			komponente.SetBesitzerSpielakteur(akt);
			akt.AddKomponente(komponente);
			//PhysikKomponente
			AkteurKomponente komponente2 = createKomponente((String)auto[2],auto);
			komponente2.SetBesitzerSpielakteur(akt);
			akt.AddKomponente(komponente2);
			
			return akt;
	}
	
	
	/**
	 * Initialisere Eine Komponente. Komponententyp gibt an welche
	 * @param komponentenTyp String Komponententyp
	 * @param auto	Object[] liste der Autoeigenschaften
	 * @return AkteurKomponente
	 */
	private AkteurKomponente createKomponente(String komponentenTyp, Object[] auto) {
		
		switch(komponentenTyp) {
		
		case "RotesAutoPhysikKomponente":
			RotesAutoPhysikKomponente komp = new RotesAutoPhysikKomponente();
			komp.masse = (int) auto[3]; 
			komp.maxGeschwindigkeit = (int) auto[4];			
			komp.fahrKraftsteigerung = (int) auto[5];

			return komp;
		case "RotesAutoAkteurDatenKomponente":
			RotesAutoAkteurDatenKomponente dkomp = new RotesAutoAkteurDatenKomponente();
			return dkomp;
		
		case "BlauesAutoPhysikKomponente":
			BlauesAutoPhysikKomponente bkomp = new BlauesAutoPhysikKomponente();

			bkomp.masse = (int) auto[3]; 
			bkomp.maxGeschwindigkeit = (int) auto[4];			
			bkomp.fahrKraftsteigerung = (int) auto[5];

			return bkomp;
		case "BlauesAutoAkteurDatenKomponente":
			BlauesAutoAkteurDatenKomponente bdkomp = new BlauesAutoAkteurDatenKomponente();
			return bdkomp;
		
		case "GruenesAutoPhysikKomponente":
			BlauesAutoPhysikKomponente gkomp = new BlauesAutoPhysikKomponente();

			gkomp.masse = (int) auto[3]; 
			gkomp.maxGeschwindigkeit = (int) auto[4];			
			gkomp.fahrKraftsteigerung = (int) auto[5];

			return gkomp;
		case "GruenesAutoAkteurDatenKomponente":
			GruenesAutoAkteurDatenKomponente gdkomp = new GruenesAutoAkteurDatenKomponente();
			return gdkomp;
	
		case "GelbesAutoPhysikKomponente":
			BlauesAutoPhysikKomponente gekomp = new BlauesAutoPhysikKomponente();

			gekomp.masse = (int) auto[3]; 
			gekomp.maxGeschwindigkeit = (int) auto[4];			
			gekomp.fahrKraftsteigerung = (int) auto[5];

			return gekomp;
		case "GelbesAutoAkteurDatenKomponente":
			GelbesAutoAkteurDatenKomponente gedkomp = new GelbesAutoAkteurDatenKomponente();
			return gedkomp;
}
		
		return null;
	}
	
}
