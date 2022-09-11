package spielansichtsschicht;




import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;
import gui.Spielfenster;
import gui_opengl.Window;
import spiellogikschicht.Spielstadien;

/**
 * 
 * @author André
 *Eine Ansicht die fuer den menschen ist.
 *Wird von der Anwendungsschicht einmal in einer schleife upgedatet
 */

/*
 * es Fehlen noch interne Spielobjekte die alles Repraesentieren grafisch Fahrzeuge update und render Methoden aufruf
 * key Events an die Spiellogik
 * Reagiert auf Events wie Spielobjekt update(Koordinaten), Spielstatus aendern, auch auf Mapreading instanz von einer Strecke 
 * movement Controller
 */
public class MenschlicheAnsicht implements ISpielAnsicht, EventListener {

//	public int breite, hoehe = 600; 
	/**
	 * grafisches Spielfenster der menschlichen Ansicht
	 */
	public Spielfenster spielFenster; //Spielfenster
	/**
	 * grafisches Spielfenster der menschlichen Ansicht wenn man OpenGL benutzt
	 */
	public Window spielFensterOpenGL;
	/**
	 * Aktueller Spielstatus
	 */
	public Spielstadien status = Spielstadien.Status_Initialisieren;
		
	protected EventManager event;

	protected Spieloptionen optionen;
	/**
	 * Controller der die Spieldaten verwaltet
	 */
	protected Controller controller; //Klasse, die alle grafischen Spieldaten verwaltet.
	protected SpielObjekteManager spielObjektManager = new SpielObjekteManager(); //Manager, der die grafisch benoetigten Spielfigur Daten verwaltet
	protected SpielFeldManager spielfeld = new SpielFeldManager() ;				//Manager, der die grafischen Spielfeld/Karten Daten beinhaltet.
	protected SpielDatenManager spielDaten = new SpielDatenManager();				//Manager, der die grundsaetzlichen SpielFensterDaten beinhaltet.
	protected TasterturHandler tastH = new TasterturHandler();
	public String countdown = "";
	/**
	 * Enthaelt die finalen Spieldaten, wird vom Server uebertragen
	 */
	public Object[] finaleSpieldaten;
	
	/**
	 * AnsichtsID, Identifikaiton der Ansicht
	 */
	protected int AnsichtID;
	/**
	 * ID des Spielers dem diese Ansicht zugewiesen wurde
	 */
	protected int SpielerID;
	
	
	@Override
	/**
	 * Gebe zurueck das es sich um eine menschliche Spielansicht handelt
	 */
	public SpielAnsichtTyp VGetType() {
		return SpielAnsichtTyp.SpielAnsicht_Mensch;
	}
	
	@Override
	/**
	 * Festlegung das die menschliche Ansicht die ID 1 hat
	 */
	public int VGetID() { //Menschliche_Ansicht hat ID 1
		return 1;
	}
	
	@Override
	/**
	 * Weise die Spielansicht einem Spieler zu
	 */
	public void AngefuegterSpieler(int vid, int spid) {
		AnsichtID = vid;
		SpielerID = spid;
	}
	
	
	@Override
	/**
	 * uebertragen des Zeichenimpulses an das SpielPanel
	 */
	public void Render(double fTime, float fvergangeZeit) { //Aufgerufen, um die Ansicht darzustellen
		spielFenster.getPanel().Render();
	}
	
	@Override
	/**
	 * Weitergabe des Updateimpulses an das SpielPanel
	 */
	public void Update(float deltaMs) {
		spielFenster.getPanel().Update();
		
	}
	
	/**
	 * Konstruktur,  Initialiserung der SpieldatenManager in den Controller, sowie anmelden beim EventManager fuer bestimmte Ereignisse
	 */
	public MenschlicheAnsicht() {
		optionen = Spieloptionen.getInstance();

		event = EventManager.getInstance();
		event.subscribe("aendere_status_warten", this);
		event.subscribe("aendere_status_start", this);
		event.subscribe("aendere_status_laeuft", this);
		event.subscribe("aendere_status_countdown", this);
		event.subscribe("aendere_status_beendet", this);
		
		controller = new Controller(this,spielDaten,spielfeld,spielObjektManager,tastH);
		spielfeld.addController(controller); //Fuer das Zeichnen der Spielfeldes
		spielObjektManager.addController(controller); //Fuer das Zeichnen der SpielObjekte
		tastH.addController(controller);

		if(!optionen.isOpenGL) {
			spielFenster = new Spielfenster(controller);

		}
		else {
			//spielFensterOpenGL = SpielfensterOpenGL.get();
			//spielFensterOpenGL.Initial(controller);
			//spielFensterOpenGL.run();
		}
		
	}
	
	/**
	 * Der Ansicht einen Spieler zuweisen
	 * @param akteurid
	 */
	void VSetzeKontrollAkteur(int akteurid) { //Hift dem Netzwerksystem eine Ansicht dem richtigen Spieler zuzuordnene
		SpielerID =akteurid;	
	}
	
	@Override
	/**
	 * Ereignisverwaltung, eingehende Ereignisse werden hier verarbeitet
	 */
	public void updateEvent(String eventType, Object... eventData) {
		
		if(eventType == "aendere_status_warten") {
			status = (Spielstadien) eventData[0];
		}
		if(eventType == "aendere_status_start") {
			status = (Spielstadien) eventData[0];
		}
		if(eventType == "aendere_status_laeuft") {
			status = (Spielstadien) eventData[0];
		}
		if(eventType == "aendere_status_countdown") {
			countdown = (String) eventData[0];
		}
		if(eventType == "aendere_status_beendet") {
			status = (Spielstadien) eventData[0];
			finaleSpieldaten = (Object[]) eventData[1];
			Object[] finaleSpieldaten = (Object[]) eventData[1];
			double sieger = (double)finaleSpieldaten[finaleSpieldaten.length-1];

			System.out.println("Ergebnisse:");
			optionen.werteErgebnisseAus(finaleSpieldaten, true);

			if(optionen.spielerID == sieger) {
				System.out.println("Ich habe gewonnen!");
			}
			else {
				System.out.println("Ich habe verloren.");
			}
		}	
	}
}
