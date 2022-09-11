package anwendungsschicht;


import gui_opengl.MenschlicheAnsichtOpenGL;
import kiansichtsschicht.KIAnsicht;
import spielansichtsschicht.MenschlicheAnsicht;
import spiellogikschicht.BasicSpiellogik;

/**
 * 
 * @author André
 * Diese akstrakte Klasse behinhaltet, die Operationen mit der Hardware wie Dateien einlesen, 
 * Datenstrukturen die Spieloptionen beinhalten, initialisation der Spiel-Logik, 
 * der Event Manager und der NetzwerkKommunikationsmanager
 * 
 * Diese Klasse ist als Basisklasse fuer die Rennspiel_SpielApplikation gedacht.
 */
public abstract class SpielApplikation {
	/**
	 * Variable die Anzeigt ob die Hauptschleife aufgefuehrt wird
	 */
	boolean istRunning; //wahr wenn das Spiel initialisiert wurde und die hauptschleife laeuft
	/**
	 * Wird biem beenden des Spiels  auf wahr gesetzt
	 */
	boolean istBeenden; //wahr wenn das Spiel beendet wird
	
	//Map<String, String> textRessourcen; //Map wo alle TextResourcen zu finden sind
	/**
	 * Instanz der Anwendungsschicht
	 */
	SpielApplikation spielApp; //Instanz von der Anwendungsschicht
	/**
	 * Instanz der Spiel-Llogik-Schicht
	 */
	BasicSpiellogik spiellogik; //instanz einer Spiellogik
	static Spieloptionen optionen;		//Die Optionen fuer ein Spiel
	/**
	 * Instanz des eventManagers
	 */
	EventManager eventManager; //Der EventManager
	
	/**
	 * Instanz der Netzwerkverbindungsklasse
	 */
	Netzwerkverbindung netzwerk = null; 		//Management ueber das netzwerk kann sowohl client als auch server sein
	//NetzwerkEventLeiter netzwerkeventleiter;//Leitet die Events ueber das netzwerkweiter
	
	public SpielApplikation() {
		istRunning = false;
		istBeenden = false;
		spielApp = this;
		spiellogik = null;
		optionen = null;
		eventManager = null;
//		netzwerkverbindung = null;
	};	//Konstruktor
	
	/**
	 * abrakte Klasse: Gebe den Titel des Spiels zurueck
	 * @return
	 */
	public abstract String VGetSpielTitel();					//Title des Spiels
	
	/**
	 * abstract Funktion die Spiellogik und Spielansicht initialisert
	 * @return
	 */
	public abstract BasicSpiellogik SchaffeSpielUndAnsicht();	//Initiallisiere die Spiellogik und die Ansichten

	/**
	 * Schaffe eine neue KI Ansicht
	 */
	public KIAnsicht getKIAnsicht(String [] agrs){
		KIAnsicht ki_ansicht = new KIAnsicht(agrs);
		return ki_ansicht;
	}


	/**
	 * Schaffe eine neue Menschliche Ansicht
	 * @return MenschlicheAnsicht
	 */
	public MenschlicheAnsicht getMenschlicheAnsicht() { //Schaffe eine menschliche Ansicht fuer das Spiel
		MenschlicheAnsicht m_ansicht = new MenschlicheAnsicht(); //Vielleicht Status mitgeben fuer anzeigen des Hauptmenues
		return m_ansicht;
	}

	/**
	 * Schaffe eine neue Menschliche Ansicht für OpenGL
	 * @return MenschlicheAnsicht
	 */
	public MenschlicheAnsichtOpenGL getMenschlicheAnsichtOpenGL() { //Schaffe eine menschliche Ansicht fuer das Spiel
		MenschlicheAnsichtOpenGL m_ansicht = new MenschlicheAnsichtOpenGL(); //Vielleicht Status mitgeben fuer anzeigen des Hauptmenues
		return m_ansicht;
	}
	
	/**
	 * Anfordern der Spiellogik
	 * @return Instanz der Spiellogik
	 */
	public BasicSpiellogik GetSpielLogik() {//Liefere die Spiellogik
		return spiellogik;
	}
	
	
	/**
	 * Kontrolle ob die Hauptschleife weiter ausgefuehrt werden soll
	 * @return
	 */
		public boolean isRunning() {
			return istRunning;
		}
	
	/**
	 * UpdateFunktion, weiterleten des Updateimpuls zur Spiellogik
	 * @param fTime
	 */
	public void Update(double fTime) { //Zeit in Sekunden
		spielApp.spiellogik.VUpdate((float)fTime);
	}

	
}
