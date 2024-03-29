package spiellogikschicht;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spielansichtsschicht.ISpielAnsicht;
import spielansichtsschicht.SpielAnsichtTyp;

	/**
	 * Klasse die die Grundlegenden Komponenten einer Spiellogik beinhaltet
	 * @author André
	 *
	 */
public abstract class BasicSpiellogik implements ISpiellogik, EventListener{

	public float lebenszeit;					//zeigt an wie lange das Spiel gelaufen ist //Debug
	/**
	 * HashMap mit allen Spielakteuren, Schluessel = int SpielerID, Wert = Spielakteur
	 */
	public Map<Integer, Spielakteur> akteure = null;	//Liste mit allen Spielakteuren
	/**
	 * Letzte vergebende SpielakterID
	 */
	public int letzteAkteurId;
	/**
	 * Aktuelle Spielstatus
	 */
	Spielstadien status;						//AktuellerSpiestatus
	/**
	 * alle Spielansichten die mit dem Spiel verbunden sind
	 */
	public List <ISpielAnsicht> spielAnsichten;		//Liste aller Ansichten, die mit dem Spiel verbunden sind
	/**
	 * Instanz der Spielakteur Fabrik
	 */
	AkteurFabrik fabrik;	
	/**
	 * Instanz der Klasse KollisionChecker
	 */
	KollisionChecker check;						
	/**
	 * EreignisManager
	 */
	EventManager event;
	//int externeSpielerID;						//Sind wir eine externer Spieler wird hier unsere Socket nummer Auf dem Server gespeichert
//	ISpielPhysik physik;						//Pysiksystem
	/**
	 * Instanz des Kartenamangers
	 */
	KartenManager kartenmanager;					//Verwaltung der Spielkarten
	/**
	 * Zugang zu den Spieloptionen
	 */
	Spieloptionen optionen;
	/**
	 * Zeitpunkt an dem das Spiel gestatet ist.
	 */
	Date spielstart;
	/**
	 ** Zeitpunkt an dem das Spiel beendet ist.
	 */
	Date spielbeendet;
	/**
	 * Aktueller Countdownwert
	 */
	int countdown = 4; //Start countdown
	boolean spiellaeuft = true;
	
	/**
	 * Intitialiserung der Spiel-Logik mit default werten
	 */
	public BasicSpiellogik() { //Inizialisierung aller Varriablen auf einen Default Wert
		lebenszeit=0;
		status = Spielstadien.Status_Initialisieren;
		letzteAkteurId=0;
		fabrik = null;
		
		event = EventManager.getInstance();
		optionen = Spieloptionen.getInstance();
		kartenmanager = new KartenManager(); //initialisierung des Kartenmanagers
		check = new KollisionChecker(this);
	}
	
	BasicSpiellogik(String str){
		if(str.equals("client")) {
			event = EventManager.getInstance();
			optionen = Spieloptionen.getInstance();
		}
	}
	/**
	 * Initialise Systeme, wie Fabrik
	 * @return
	 */
	public boolean Init() { //Initialisierungsfunktionen
		fabrik = ErschaffeFabrik();//erschaffe Fabrick
		return true;
	}
	/**
	 * Liefert eine neue Spielakteur ID
	 * @return int SpielakteurID
	 */
	public int GetNeueAkteurID(){						//Wei�t eine neue Nummer zu fuer einen weiteren Akteur
	return ++letzteAkteurId;	
	}

	/**
	 * Fuege der Ansichtsliste eine neue SpielAnsicht hinzu
	 * @param ansicht Die neue SpielAnsicht
	 * @param akteurID SpielerID
	 */
	public void VHinzufuegenSpielansicht(ISpielAnsicht ansicht, int akteurID) {
		if(spielAnsichten == null) {
			spielAnsichten = new ArrayList<ISpielAnsicht>();
		}
		int Vid = (int) spielAnsichten.size(); //Gibt eine neue AnsichtsID von Groe�e des List<Containers>
		spielAnsichten.add(ansicht);
		ansicht.AngefuegterSpieler(Vid, akteurID);
		//Event fuege Spielfigur hinzu
		
	} 
	
	public KartenManager GetKartenManager() {return kartenmanager;}
	

	@Override //ErschaffeAkteur
	/**
	 * Erschaffe einen neuen Spielakteur
	 * @param ID SpielerID
	 * @param spielerresource Autotyp (String)
	 */
	public Spielakteur VErschaffeSpieler(int ID, String spielerresource) {

		//Fabrik aber Testweise manuell
		
		if(akteure == null) {
			akteure = new HashMap<Integer, Spielakteur>();
		}
		
		Spielakteur akt = fabrik.erschaffeAkteur(ID,spielerresource);
		akteure.put(akt.GetId(), akt);
		event.notify("add_view_car", akt.GetId(), spielerresource, optionen.startpositionenArray.get(akt.GetId())[0],optionen.startpositionenArray.get(akt.GetId())[1],optionen.startpositionenArray.get(akt.GetId())[2]);//Event auto initisieren und view hinzufuegen //Startposition und direction mitgebenaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
			
		return null;
	}
	
	/**
	 * Erschaffe einen neuen Spielakteur
	 * @param SpielerID Integer
	 * @param AutoID Autotyp (int)
	 */
	public void fuegeSpielakteurHinzu(int SpielerID, int AutoID) {
		String autotyp = "";
		
		if(akteure == null) {
			akteure = new HashMap<Integer, Spielakteur>();
		}
		
		for (Map.Entry<String, Object[]> entry : optionen.autoListe.entrySet()) {		
			Object[] o = entry.getValue();
			if((int)o[0] == AutoID) {
				autotyp = entry.getKey();
			}
	    }
		
		Spielakteur akt = fabrik.erschaffeAkteur(SpielerID,autotyp);
		akteure.put(SpielerID, akt);
		//Vielleicht event.notify("add_view_car");
		event.notify("add_view_car", akt.GetId(), autotyp, optionen.startpositionenArray.get(akt.GetId())[0],optionen.startpositionenArray.get(akt.GetId())[1],optionen.startpositionenArray.get(akt.GetId())[2]);
		
	}
	
	//Logik Update
	@Override
	/**
	 * Verwaltet den Spielablauf und Stausaenderungen
	 */
	public void VUpdate(float fTime/* float VergangenZeit*/) { //Zeit in Sekunden
		//Update aller Ansichten
		if(spielAnsichten!=null) {
		for(ISpielAnsicht ansicht : spielAnsichten ) {
			ansicht.Update(fTime);
		}
		}
		
	if(optionen.client == false) { //Nur updaten und berechnen wenn man Server ist
	switch(status) {
	case Status_Initialisieren:
		//wenn wir hier sind ist alles fertig initialisiert
		VAendereStatus(Spielstadien.Status_WartenAufSpieler);
		event.notify("aendere_status_warten ", Spielstadien.Status_WartenAufSpieler);
		break;
		
	case Status_WartenAufSpieler:
		if(akteure != null && optionen.maxAnzahlSpieler == akteure.size()) {
			VAendereStatus(Spielstadien.Status_Spiel_starten);
			event.notify("aendere_status_start", Spielstadien.Status_Spiel_starten);
			spielstart = new Date() ; //Setzten eines neuen Spielstartes
		}
		break;
	case Status_Spiel_starten:
		//Startevent
		Date jetzt = new Date();  //Jetztie Zeit
		int sekunden = (int) (jetzt.getTime()-spielstart.getTime())/1000; //differenz in Sekunden
		
		if(sekunden <= 2/* && countdown == 4*/) {
			//countdown = 3; //Setzte den coutndownvaribel herunter
			event.notify("aendere_status_countdown", "3");
			System.out.println("3");
		}
		else if(sekunden <= 3 /*&& countdown == 3*/) {
			//countdown = 2; //Setzte den coutndownvaribel herunter
			event.notify("aendere_status_countdown", "2");
			System.out.println("2");
		}
		else if(sekunden <= 4 /*&& /*countdown == 2*/) {
			//countdown = 1; //Setzte den coutndownvaribel herunter
			event.notify("aendere_status_countdown", "1");
			System.out.println("1");
		}
		else if(sekunden <= 5 /*&& countdown == 1*/) {
			//countdown = 0;
			System.out.println("Start ");
			event.notify("aendere_status_countdown", "Start");
		}
			//Send Event Start
			else if(sekunden <=6) {
			VAendereStatus(Spielstadien.Status_Spiel_Laeuft);
			event.notify("aendere_status_laeuft", Spielstadien.Status_Spiel_Laeuft);
			spielstart = new Date(); //Fuer neue Berechnungen zuruecksetzten
		}
		
		break;
	case Status_Spiel_Laeuft:
		
		Date jetzt1 = new Date();  //Jetztie Zeit
		int sekunden1 = (int) (jetzt1.getTime()-spielstart.getTime())/1000; //Zeitdifferenz in Sek
		
		if(sekunden1 <= optionen.spiellaenge) {
			//CollisionsCheck
			check.update();
			
			//Update aller Akteure
			if(akteure != null) {
				for (Map.Entry<Integer, Spielakteur> entry : akteure.entrySet()) {
					entry.getValue().update();

					if(optionen.spielmodus == 2) {
						//TODO: Anzahl Runden zaehlen
						for(int i = 1; i < optionen.maxAnzahlSpieler; i++) {
							AKomponenteAkteurDaten daten = (AKomponenteAkteurDaten) entry.getValue().GetKomponente(1);
							if(daten.anzahlRunden == 3) {
								//SendEvent Spiel beendet
								VAendereStatus(Spielstadien.Status_Spiel_Beendet);
							}
						}
					}
			        //System.out.println(entry.getKey() + " = " + entry.getValue()); 
			    }
			}
			
		}
		else {
			//SendEvent Spiel beendet
			VAendereStatus(Spielstadien.Status_Spiel_Beendet);
		}
		break;
	
	case Status_Spiel_Beendet:
		//Event Statisik
		if(spiellaeuft) {
		spielbeendet = new Date();
		System.out.println("Game Over");
		Object o [] = new Object[akteure.size()*3] ;
		if(akteure != null) {
			//Object o [] = new Object[akteure.size()*3] ;
			int i = 0;
			for (Map.Entry<Integer, Spielakteur> entry : akteure.entrySet()) {
				AKomponenteAkteurDaten daten = (AKomponenteAkteurDaten) entry.getValue().GetKomponente(1);
				//System.out.println("AnzahlRunden:" + daten.anzahlRunden + "BesteZeit: " + (daten.besteRundenZeit/1000));
				o[i*3] = daten.akteur.GetId(); //Daten ins Object schreiben
				o[i*3+1] = daten.anzahlRunden;
				o[i*3+2] = (daten.besteRundenZeit/1000);
				i++;
				//event.notify("ergebnis_uebertragen", o);
		        //System.out.println(entry.getKey() + " = " + entry.getValue()); 
		    }
		}
		event.notify("aendere_status_beendet", Spielstadien.Status_Spiel_Beendet, o);
		spiellaeuft = false;

		//Make JSON for Winner-statistics
			speichereStatisikDaten();
		}

		Date aktuelleZeit = new Date();
		if(aktuelleZeit.getTime() > spielbeendet.getTime() + 5000 && !optionen.client){
			System.exit(0);
		}


		break;
		}	
		}
	}
	
	//Aendere SPielstadien
	@Override
	/**
	 * aendere den Spielstatus
	 * @param neuerSpielstatus
	 */
	public void VAendereStatus(Spielstadien neuerStatus) {
		status = neuerStatus;
		optionen.status = neuerStatus;
	}
	public Spielstadien GetSpielstatus() {
		return status;
	}
	
	/**
	 * Leite den Zeichenimplus an alle menschlichen Spielansichten weiter
	 * @param zeit
	 */
	public void Zeichnen(double zeit) { //Leitet den Impuls des Zeichnens von der Anwendungsschicht weiter an die Anwendungschicht
		if(spielAnsichten!=null) {
		for(ISpielAnsicht ansicht : spielAnsichten) {
			if(ansicht.VGetType() == SpielAnsichtTyp.SpielAnsicht_Mensch) {
				ansicht.Render(zeit,0);
			}
		}
		}
	}
	
	/**
	 * Erschaffe die AkteurFabrik
	 * @return AkteurFabrik
	 */
	protected AkteurFabrik ErschaffeFabrik() {
		fabrik = new AkteurFabrik();
		return fabrik;
	};

	public void speichereStatisikDaten(){

		String saveFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".json";
		String playerFile = "Spielerstatistik_" + saveFile;

		JSONObject mainObj = new JSONObject();
		JSONArray array = new JSONArray();

		Map<Integer, Integer> spielerPositionen = ermittleSieger();

		for (Map.Entry<Integer, Spielakteur> entry : akteure.entrySet()) {
			AKomponenteAkteurDaten data = (AKomponenteAkteurDaten) entry.getValue().GetKomponente(1);
			JSONObject jo = new JSONObject();

			jo.put("Spielernummer", entry.getValue().GetId());
			jo.put("Rundenanzahl", data.anzahlRunden);
			jo.put("BesteRundenZeit", data.besteRundenZeit);
			jo.put("Rundenzeiten", data.rundenZeiten);
			jo.put("TileKollisionAnzahl", data.anzahlWandKollisionen);
			jo.put("SpielerKollisionAnzahl", data.anzahlSpielerKollisionen);
			jo.put("Endplatzierung", spielerPositionen.get(entry.getValue().GetId()));

			array.add(jo);

		}
		mainObj.put("StartTimestamp", spielstart.toString());
		mainObj.put("EndTimestamp", spielbeendet.toString());
		mainObj.put("Spielmodus", optionen.spielmodus );
		mainObj.put("Spielerdaten", array);

		//Save to file
		try {
			FileWriter file = new FileWriter(playerFile);
			file.write(mainObj.toString());
			file.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

		void bubbleSort(double arr[])
		{
			int n = arr.length;
			for (int i = 0; i < n - 1; i++)
				for (int j = 0; j < n - i - 1; j++)
					if (arr[j] > arr[j + 1]) {
						// swap arr[j+1] and arr[j]
						double temp = arr[j];
						arr[j] = arr[j + 1];
						arr[j + 1] = temp;
					}
		}

	public Map<Integer, Integer> ermittleSieger() {
		//Beste Rundenzeit
		if (optionen.spielmodus == 1) {

			double darray[] = new double[akteure.size()];
			Map<Integer, Double> rundenzeiten = new HashMap<Integer, Double>();
			Map<Integer, Integer> plazierungSieger = new HashMap<Integer, Integer>();

			int i = 0;
			for (Map.Entry<Integer, Spielakteur> entry : akteure.entrySet()) {
				AKomponenteAkteurDaten data = (AKomponenteAkteurDaten) entry.getValue().GetKomponente(1);
				rundenzeiten.put(entry.getKey(), data.besteRundenZeit);
				darray[i] = data.besteRundenZeit;
				i++;
			}

			bubbleSort(darray);

			for (int j = 0; j < darray.length; j++) {
				for (Map.Entry<Integer, Double> entry : rundenzeiten.entrySet()) {
					if (entry.getValue() == darray[j]) {
						plazierungSieger.put(entry.getKey(), j + 1);
					}
				}
			}
			return plazierungSieger;
		}
		//Schnellsten 3 Runden
		else if (optionen.spielmodus == 2) {
			//TODO
			Map<Integer, Integer> plazierungSieger = new HashMap<Integer, Integer>();

			for (Map.Entry<Integer, Spielakteur> entry : akteure.entrySet()) {
				AKomponenteAkteurDaten data = (AKomponenteAkteurDaten) entry.getValue().GetKomponente(1);

				int i = 1; //Erster Platz
				for (Map.Entry<Integer, Spielakteur> entry2 : akteure.entrySet()) {
					AKomponenteAkteurDaten data2 = (AKomponenteAkteurDaten) entry2.getValue().GetKomponente(1);
					if (entry.getValue().GetId() != entry2.getValue().GetId()) {
						if (data.anzahlRunden > data2.anzahlRunden) {
							continue;
						}
						else if(data.anzahlRunden == data2.anzahlRunden){
							if (data.checkpoints >= data2.checkpoints) {
								continue;
							} else {
								i++;
							}
						}
						else {
							i++;
						}
					}
				}
					plazierungSieger.put(entry.getKey(), i);
			}

			return plazierungSieger;
		}
		return null;
	}

	
}
