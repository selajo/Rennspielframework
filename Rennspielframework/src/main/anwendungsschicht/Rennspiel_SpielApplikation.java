package anwendungsschicht;


import gui_opengl.MenschlicheAnsichtOpenGL;
import kiansichtsschicht.KIAnsicht;
import replayansichtsschicht.ReplayAnsicht;
import spielansichtsschicht.ISpielAnsicht;
import spielansichtsschicht.MenschlicheAnsicht;
import spiellogikschicht.BasicSpiellogik;
import spiellogikschicht.Rennspiellogik;

import static java.lang.Thread.sleep;

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
		boolean init = false;
		int SpielerID = -1;


		long startZeit = System.nanoTime();//Zeit wo gestartet wird.
		long timer = System.currentTimeMillis();
		while(istRunning) {
			//Rechnen die Differnz der Zeit aus
			long jetztZeit = System.nanoTime();
			uDeltaZeit += (jetztZeit - startZeit);
			fDeltaZeit += (jetztZeit - startZeit);
			startZeit = jetztZeit;
			
			if (uDeltaZeit >= uOptimale_Zeit) {

				if(init == false && optionen.client == false && !CLI.isHeadless() ){
					ISpielAnsicht ansicht;
					//Erste Ansicht hinzufuegen
					if(!optionen.isOpenGL){
						ansicht = getMenschlicheAnsicht(); //menschlicheAnsicht geschaffen
					}
					else{
						ansicht = getMenschlicheAnsichtOpenGL();
					}
					if(optionen.SpielerAufServer == true && optionen.client == false && optionen.SpielerAufServer) {
						SpielerID = spiellogik.GetNeueAkteurID();
						spiellogik.VHinzufuegenSpielansicht(ansicht, SpielerID); //Fuege eine Menschliche Ansicht hinzu (-1 Vorlaeufig wegen nummer fuer Spieler(id))
						//Inizialisieren einer Spiellogik
					}
					else {
						spiellogik.VHinzufuegenSpielansicht(ansicht, -1); //-1 fuer Ansicht ohne einen Spieler
					}
				}

				Update(uDeltaZeit/1000000000); //nicht ganz richtig aber fast Update Zeit in Sekunden
				update ++;
				uDeltaZeit -= uOptimale_Zeit; //Zuruecksetzen der Variable fuer naechsten Durchgang;

				if(init == false && optionen.client == false && optionen.SpielerAufServer && !CLI.isHeadless()){
					spiellogik.VErschaffeSpieler(SpielerID,"GruenesAuto");	//Vielleicht auch auswahl treffen fuer einen Spieler
				}
				init = true;
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
	 */
	public Rennspiel_SpielApplikation() {
			super();
			if(CLI.isClient()) {
				initialisiereClient();
			}
			else if(CLI.isServer()) {
				initialisiereServer();
			}
	}
	

	/**
	 * Initialisiere eine CLientanwendung, mit Spiellogik und Spielansicht
	 */
	private void initialisiereClient() {
		
		optionen = Spieloptionen.getInstance();
		optionen.setClientSpieloptionen();
	
		schaffeClientSpiel(); //starte spiellogik//Pseudomaesssig

		netzwerk = new Netzwerkverbindung("client", this);
		while(optionen.spielfeldTiles == null || optionen.autoTiles == null || optionen.mapTileNum == null) {

		try {
			sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

		if(CLI.isKI()) {
			//Initialisiere eine KI ansicht
			KIAnsicht ansicht = getKIAnsicht();
			spiellogik.VHinzufuegenSpielansicht(ansicht, netzwerk.id); //setzt menschliche Spielansicht
		}
		else if(CLI.isMensch()) {
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
		else if(CLI.isReplay()) {
			//Initialisiere eine Replay Ansicht
			ReplayAnsicht replayAnsicht = new ReplayAnsicht(CLI.getReplay());
			spiellogik.VHinzufuegenSpielansicht(replayAnsicht, netzwerk.id);
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
	 */
	private void initialisiereServer() {
		optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen(CLI.getConfig());
		
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
				sleep(500);
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

		/*ISpielAnsicht ansicht;
		
		//Erste Ansicht hinzufuegen
		if(!optionen.isOpenGL){
		ansicht = getMenschlicheAnsicht(); //menschlicheAnsicht geschaffen
		}
		else{
		ansicht = getMenschlicheAnsichtOpenGL();
		}

		//TODO vielleicht in schleife
		if(optionen.SpielerAufServer == true) {
		int id = spiellogik.GetNeueAkteurID();
		spiellogik.VHinzufuegenSpielansicht(ansicht, id); //Fuege eine Menschliche Ansicht hinzu (-1 Vorlaeufig wegen nummer fuer Spieler(id))
		spiellogik.VErschaffeSpieler(id,"BlauesAuto");	//Vielleicht auch auswahl treffen fuer einen Spieler	
						//Inizialisieren einer Spiellogik
		}
		
		else {
			spiellogik.VHinzufuegenSpielansicht(ansicht, -1); //-1 fuer Ansicht ohne einen Spieler	
		}*/
		
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
