package anwendungsschicht;


import gui_opengl.MenschlicheAnsichtOpenGL;
import kiansichtsschicht.KIAnsicht;
import spielansichtsschicht.ISpielAnsicht;
import spielansichtsschicht.MenschlicheAnsicht;
import spiellogikschicht.BasicSpiellogik;
import spiellogikschicht.Rennspiellogik;

/**
 * 
 * @author André
 *Implementierung der fuer das Spiel genutzten SpielApplikation-Anwendungschicht.
 */
public class Rennspiel_SpielApplikation extends SpielApplikation implements Runnable{
	/**
	 * SpielThread
	 */
	private Thread gameThread;
	/**
	 * Maximale Anzahl von der maximalen anzahl an Frames Per Secound (FPS)
	 */
	final int MAX_FPS = 60;
	/**
	 * Maximale Anzahl von der maximalen anzahl an Frames Per Secound (FPS)
	 */
	final int MAX_UPD = 60; //Updates pro Sekunde
	final double fOptimale_Zeit = 1000000000 / MAX_FPS;
	final double uOptimale_Zeit = 1000000000 / MAX_UPD;
	/**
	 * Feststellen des aktuellen Update/Zeichen Wertes
	 */
	int zeichnen, update = 0;
	/**
	 * Zeit bis zum naechsten Update
	 */
	double uDeltaZeit = 0, fDeltaZeit =0; //Zwischenspeicher zeit
	/**
	 * Pfad des Config-Files
	 */
	String ConfigFile = "";
	
	@Override
	/**
	 * Start des Spielthread und damit der Spielschleife
	 */
	public void run() { //Spielschleife
		long startZeit = System.nanoTime();//Zeit wo gestartet wird.
		long timer = System.currentTimeMillis();
		while(istRunning) {
			//Rechnen die Differnz der Zeit aus
			long jetztZeit = System.nanoTime();
			uDeltaZeit += (jetztZeit - startZeit);
			fDeltaZeit += (jetztZeit - startZeit);
			startZeit = jetztZeit;
			
			if (uDeltaZeit >= uOptimale_Zeit) {
				Update(uDeltaZeit/1000000000); //nicht ganz richtig aber fast Update Zeit in Sekunden
				update ++;
				uDeltaZeit -= uOptimale_Zeit; //Zuruecksetzen der Variable fuer naechsten Durchgang;
		
			}
			if (fDeltaZeit >= fOptimale_Zeit) {
				Render(fDeltaZeit/1000000000); //nicht ganz richtig aber fast //Zeichne
				zeichnen ++;
				fDeltaZeit -= fOptimale_Zeit; //Zuruecksetzen der Variable fuer naechsten Durchgang;
				
			}
			
			if(System.currentTimeMillis() - timer >= 1000) { //Ausgabe der Spielgeschwindigkeit auf der Konsole
				//System.out.println("UPS: " + update +", FPS " + zeichnen);
				zeichnen = 0;
				update = 0;
				timer +=1000;
			}	
		}	
		stop();
		
	}
	
	/**
	 * Auswahl auf Basis der Paramter, ob das Spiel als Client oder Server Anwendung gestartet werden soll
	 * @param agrs Spielparameter
	 */
	public Rennspiel_SpielApplikation(String [] agrs) {
			super();
			String impdef = (String)agrs[0];
			
			
			//Test
			if(impdef.equals("client")) {
				
				initialisiereClient(agrs);
			}
			else if(impdef.equals("server")) {
				initialisiereServer(agrs);
			}
	}
	

	/**
	 * Initialisiere eine CLientanwendung, mit Spiellogik und Spielansicht
	 * @param agrs Spielparameter
	 */
	private void initialisiereClient(String [] agrs) {
		
		optionen = Spieloptionen.getInstance();
		optionen.setClientSpieloptionen(agrs);
	
		schaffeClientSpiel(); //starte spiellogik//Pseudomaesssig
		

		
		netzwerk = new Netzwerkverbindung("client", this);
		while(optionen.spielfeldTiles == null || optionen.autoTiles == null || optionen.mapTileNum == null) {

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

		String art = agrs[3];

		if(art.equals("KI")) {
			//Initialisiere eine KI ansicht
			KIAnsicht ansicht = getKIAnsicht(agrs);
			spiellogik.VHinzufuegenSpielansicht(ansicht, netzwerk.id); //setzt menschliche Spielansicht
		}
		else if(art.equals("Mensch")) {
			//Initialisiere menschliche Ansicht
			if(optionen.isOpenGL){
				MenschlicheAnsichtOpenGL ansichtOpenGL = getMenschlicheAnsichtOpenGL();
				spiellogik.VHinzufuegenSpielansicht(ansichtOpenGL, netzwerk.id);
			}
			else {
				MenschlicheAnsicht ansicht = getMenschlicheAnsicht(); //menschlicheAnsicht geschaffen
				spiellogik.VHinzufuegenSpielansicht(ansicht, netzwerk.id); //setzt menschliche Spielansicht
			}
		}

	}

	/**
	 * Initialisiere eine Spiel-Logik fuer den Client
	 */
	private void schaffeClientSpiel() {
		
		spiellogik = new Rennspiellogik("client");
	}

	/**
	 * Initialisiere eine Serveranwendung, mit Server Spiellogik und Spielansicht
	 * @param agrs Spielparameter
	 */
	private void initialisiereServer(String [] agrs) {
		
		String impdef = (String)agrs[1];
		optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen(impdef);
		
		SchaffeSpielUndAnsicht();
		netzwerk = new Netzwerkverbindung("server", this);		
	}

/**
 * /Methode die in der menschlichen Spielansichten den Impuls zum neu Zeichnen gibt
 * @param d Zeit
 */
	private void Render(double d) {
		while(spiellogik == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		spiellogik.Zeichnen(d);		
	}

	@Override
	/**
	 * Setzte den Spieltitel
	 */
	public  String VGetSpielTitel() {
		return "Rennspiel-AZ";
	}


	@Override
	/**
	 * Initialisere eine ServerSpiellogik und eine menschliche Ansicht
	 */
	public BasicSpiellogik SchaffeSpielUndAnsicht() {
		spiellogik = new Rennspiellogik(); //Schaffen einer neuen Spiellogik
		spiellogik.Init();

		ISpielAnsicht ansicht;
		
		//Erste Ansicht hinzufuegen
		if(!optionen.isOpenGL){
		ansicht = getMenschlicheAnsicht(); //menschlicheAnsicht geschaffen
		}
		else{
		ansicht = getMenschlicheAnsichtOpenGL();
		}
		
		if(optionen.SpielerAufServer == true) {
		int id = spiellogik.GetNeueAkteurID();
		spiellogik.VHinzufuegenSpielansicht(ansicht, id); //Fuege eine Menschliche Ansicht hinzu (-1 Vorlaeufig wegen nummer fuer Spieler(id))
		spiellogik.VErschaffeSpieler(id,"BlauesAuto");	//Vielleicht auch auswahl treffen fuer einen Spieler	
						//Inizialisieren einer Spiellogik
		}
		
		else {
			spiellogik.VHinzufuegenSpielansicht(ansicht, -1); //-1 fuer Ansicht ohne einen Spieler	
		}
		
		return null;
	}

	/**
	 * Starte einen neuen SpielThread
	 */
	public synchronized void start() {
		gameThread = new Thread(this); //inizialisieren von einem neuen Thread
		gameThread.start(); //Starte den SpielRead
		istRunning = true;
	}
	
	/**
	 * Stoppe den SpielThread
	 */
	public synchronized void stop() {
		istRunning = false;
	}
	
}
