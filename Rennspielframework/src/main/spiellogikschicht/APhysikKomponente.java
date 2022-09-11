package spiellogikschicht;

import java.util.HashMap;
import java.util.Map;

import anwendungsschicht.Spieloptionen;

/**
 * Abstrakte Klasse, in der Kompoenten werden alle grundlegenden Physikalischen Berechnung der Spielakteure druchgefuehrt
 * @author André
 *
 */
public abstract class APhysikKomponente extends AkteurKomponente{
	
	//private static final String Status_Spiel_Laeuft = null;
	public Spieloptionen optionen;
	/**
 	* KomponentenID der PhysikKomponente ist fest = 2
 	*/
	public final int komponentId = 2; //Alle PhysikKomponenten sollten dies Besitzen
	/**
	 * Angabe ob der Spielakteur im Kollsiionsmodus ist
	 */
	public boolean collision = false;
	/**
	 * zweidimensionaler Vektor der die Beschleunigung enthaelt
	 */
	public Vector2D beschleunigung =  new Vector2D(0,0);
	/**
	 * zweidimensionaler Vektor der die Geschwindigkeit enthaelt
	 */
	public Vector2D geschwindigkeit =  new Vector2D(0,0);;
	/**
	 * zweidimenionsaler Vektor der die zusammenaddierte Gesamtkraft enthaelt
	 */
	public Vector2D gesamtKraft=  new Vector2D(0,0);
	/**
	 * HashMap mit allen Kraftvektorn, Schluessel (art der Kraft), Werte Kraftvektoren
	 */
	public Map<String,Vector2D> kraefte = null;
	/**
	 * Masse des Fahrzeugs in kg
	 */
	public int masse = 500; //DefaultMasseAuto
	/**
	 * Letzte Zeit bei der die Physikkomponente upgedatet wurde, wird benoetigt fuer die Positionsberechnung
	 */
	public long letzteZeit = 0; //in Sekunden
	/**
	 * Hoechstgeschwindigket bis zu der die Vektoren aufgebaut werden koennen, wird durch Fabrik gesetzt, Defaultwert = 80000 
	 */
	public int maxGeschwindigkeit = 80000; //maximale Groe�e eines Beschleunigungsvektor Angabe in Newton
	/**
	 * Wert der auf einen Kraftvektor addiert wird, simmuliert die Beschleunigung
	 */
	public int fahrKraftsteigerung = 5000;	//Wert der zu einem Vektor addiert wird
	
	/**
	 * Variablen die festlegen welche Richutng das Fahrzeug fahren soll, wird gesetzt durch Spielereingaben
	 */
	public boolean down= false, up= false, left= false, right = false; //Parameter die gesetzt werden von der Tastertureingabe
	/**
	 * Variablen, die festlegen aus welcher Richtung eine Kollision entsteht
	 */
	public boolean kollisionDown = false, kollisionUp = false, kollisionLeft = false, kollisionRight = false;
	
	/**
	 * Konstruktor,
	 * Zuweisen der 4 Richtungunskrafte der KraftListe( oben, unten, links, rechts)
	 */
	public APhysikKomponente() {
		optionen = Spieloptionen.getInstance();
		addKraft("upKraft", new Vector2D(0,0));
		addKraft("downKraft", new Vector2D(0,0));
		addKraft("leftKraft", new Vector2D(0,0));
		addKraft("rightKraft", new Vector2D(0,0));
		
	}
	
	/**
	 * Hinzufuegen einer Kraft der Kraft Hash-Map
	 * @param kraft Art der Kraft (String)
	 * @param vec	Kraftvektor
	 */
	public void addKraft(String kraft, Vector2D vec) {
		if(kraefte == null) {
			kraefte = new HashMap<String,Vector2D>();
		}
		kraefte.put(kraft, vec);
	}
	
	//void updatePosition() {}
	
	/**
	 * Funktion die Kraftvektoren Aufbaut
	 */
	public void RichtungsKraftVektorenAufbauen(){
		if(up == true ||down == true ||left == true ||right == true ) { //Das nur einaml die Koordinaten bei einer aenderung upgedatet werden
			if(up == true) {
				akteur.direction = "up";
				//Nachkommastellen beseitigen
				int wert =(int) kraefte.get("upKraft").y;
				kraefte.get("upKraft").y = (double) wert;
				
				if(kraefte.get("upKraft").y >= -maxGeschwindigkeit && !kollisionUp){
				kraefte.get("upKraft").y -= fahrKraftsteigerung;
				}
			}
			if(down == true) {
				akteur.direction = "down";
				//Nachkommastellen beseitigen
				int wert =(int) kraefte.get("downKraft").y;
				kraefte.get("downKraft").y = (double) wert;
				
				if(kraefte.get("downKraft").y <= maxGeschwindigkeit && !kollisionDown){
				kraefte.get("downKraft").y += fahrKraftsteigerung;
				}
			}
			if(left == true) {
				akteur.direction = "left";
				//Nachkommastellen beseitigen
				int wert =(int) kraefte.get("leftKraft").x;
				kraefte.get("leftKraft").x = (double) wert;
				
				if(kraefte.get("leftKraft").x >= -maxGeschwindigkeit && !kollisionLeft){
				kraefte.get("leftKraft").x -= fahrKraftsteigerung;
				}
			}
			if(right == true) {
				akteur.direction = "right";
				//Nachkommastellen beseitigen
				int wert =(int) kraefte.get("rightKraft").x;
				kraefte.get("rightKraft").x = (double) wert;
				
				if(kraefte.get("rightKraft").x <= maxGeschwindigkeit && !kollisionRight){
				kraefte.get("rightKraft").x += fahrKraftsteigerung;
				}
			}
			//akteur.event.notify("update_koordinate", -1, direction, worldX, worldY);
			}
	}

	@Override
	/**
	 * UpdateImpuls sorgt dafuer das Kraftvektoren aufgebaut werden, eine Neue Position berechnet wird, sowie ungewollte Kraftvektoren wieder abgebaut werden
	 */
	public void Update() {
		if(!collision) {
		//if(up == true ||down == true ||left == true ||right == true ) {
		RichtungsKraftVektorenAufbauen();
		}
		BerechneNeuePosition();
	//}
		if(!collision) {
		RichtungsKraftVektorenAbbauen();
		}
		
		KraefteBereinigen();
		akteur.event.notify("update_koordinate", akteur.GetId(), akteur.direction, (int)akteur.position.x, (int)akteur.position.y); //Event and die Spielansicht
	}
	
	
/**
 * Funktion, die in kraftvektor sehr kleine Kraefte auf Null setzt, Sorgt fuer gleichmae�e Werte
 */
private void KraefteBereinigen() {
	for (Map.Entry<String,Vector2D> entry : kraefte.entrySet()) { //Update alle Komponenten
		if(entry.getValue().x > -(fahrKraftsteigerung/2) && entry.getValue().x < (fahrKraftsteigerung/2)) {
			entry.getValue().x = 0;
		}
		if(entry.getValue().y > -(fahrKraftsteigerung/2) && entry.getValue().y < (fahrKraftsteigerung/2)) {
			entry.getValue().y = 0;
		}	
    }
		
	}

/**
 * Berechnen einer neuen Spielakteur Position auf Basis von Kraftvektoren
 */
private void BerechneNeuePosition() {
		if(letzteZeit == 0) {
			letzteZeit = System.nanoTime(); 
		}
	
		long neueZeit = System.nanoTime(); //neue Zeit in Sekunden
		double vergangeneZeit = (double) (neueZeit - letzteZeit)/ 1000000000 *10; //ist Veraenderung der Zeit //in sekunden
		letzteZeit = neueZeit; //Setzten fuer den Naechsten Durchgang
		
		berechneGesamtKraft();
		
		//Beschleunigung
		beschleunigung = new Vector2D((gesamtKraft.x*getReibungVonPosition())/masse, (gesamtKraft.y*getReibungVonPosition())/masse); //F = m * a umgestellt und Vector division
		//Geschwindigkeit
		geschwindigkeit.add(beschleunigung = new Vector2D (beschleunigung.x * vergangeneZeit, beschleunigung.y * vergangeneZeit)); //
		//System.out.println("Geschwindichkeit: " + geschwindigkeit.toString());
		//Positionsberechnung
		Vector2D altePosition = new Vector2D(akteur.position.x, akteur.position.y);
		akteur.position.add(geschwindigkeit = new Vector2D (geschwindigkeit.x * vergangeneZeit, geschwindigkeit.y * vergangeneZeit));
		if(checkNeuePositionTileCollision()) {
			akteur.position = altePosition;
		}
	}

	/**
	 * Ermittle den Reibungskoeffizient von dem Tile auf dem sich der Spielakteur im Augenblick befindet
	 * @return double Reibungskoeffizient
	 */
	public double getReibungVonPosition() {
		KartenManager kartenmanager = KartenManager.getInstance();
		Spieloptionen optionen = Spieloptionen.getInstance();
		
		//Ermittle Mittlere Auto position
		int akteurMitteX = (int)akteur.position.x + optionen.tileGroesse/2;
		int akteurMitteY = (int)akteur.position.y + optionen.tileGroesse/2;
		
		int akteurTileMitteX = akteurMitteX / optionen.tileGroesse;
		int akteurTileMitteY = akteurMitteY / optionen.tileGroesse;
		
		double Reibung = kartenmanager.mapTiles[akteurTileMitteX][akteurTileMitteY].Reibung;
		//System.out.println(Reibung);
		return Reibung;
	}

	/**
	 * ueberpruefe ob die neue Position eine Tile Collision ausloest
	 * @return
	 */
	private boolean checkNeuePositionTileCollision() {
		KartenManager kartenmanager = KartenManager.getInstance();
		Spieloptionen optionen = Spieloptionen.getInstance();
		
		AKomponenteAkteurDaten data = (AKomponenteAkteurDaten) akteur.GetKomponente(1);
		//APhysikKomponente phy = (APhysikKomponente) akteur.GetKomponente(2);
		
		int akteurLinksWeltX = (int) akteur.position.x + data.festeCollisionBox.x;
		int akteurRechtsWeltX = (int) akteur.position.x + data.festeCollisionBox.x + data.festeCollisionBox.width;
		int akteurTopWeltY = (int) akteur.position.y + data.festeCollisionBox.y;
		int akteurBottomWeltY = (int) akteur.position.y + data.festeCollisionBox.y + data.festeCollisionBox.height;
		
		int akteurLinkeSpalte = akteurLinksWeltX/optionen.tileGroesse;
		int akteurRechteSpalte = akteurRechtsWeltX/optionen.tileGroesse;
		int akteurTopZeile = akteurTopWeltY/optionen.tileGroesse;
		int akteurBottomZeile = akteurBottomWeltY/ optionen.tileGroesse;
		
		Tile tileNummer1, tileNummer2;
		
		akteurTopZeile = (akteurTopWeltY /* - 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
		tileNummer1 = kartenmanager.mapTiles[akteurLinkeSpalte][akteurTopZeile];
		tileNummer2 = kartenmanager.mapTiles[akteurRechteSpalte][akteurTopZeile];
		if(tileNummer1.collision == true || tileNummer2.collision == true) {
			return true;
		}	

		akteurBottomZeile = (akteurBottomWeltY /*+ 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
		tileNummer1 = kartenmanager.mapTiles[akteurLinkeSpalte][akteurBottomZeile];
		tileNummer2 = kartenmanager.mapTiles[akteurRechteSpalte][akteurBottomZeile];
		if(tileNummer1.collision == true || tileNummer2.collision == true) {
			return true;
		}
		
		akteurLinkeSpalte = (akteurLinksWeltX /*- 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
		tileNummer1 = kartenmanager.mapTiles[akteurLinkeSpalte][akteurTopZeile];
		tileNummer2 = kartenmanager.mapTiles[akteurLinkeSpalte][akteurBottomZeile];
		if(tileNummer1.collision == true || tileNummer2.collision == true) {
			return true;
		}
		
		akteurRechteSpalte = (akteurRechtsWeltX /*+ 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
		tileNummer1 = kartenmanager.mapTiles[akteurRechteSpalte][akteurTopZeile];
		tileNummer2 = kartenmanager.mapTiles[akteurRechteSpalte][akteurBottomZeile];
		if(tileNummer1.collision == true || tileNummer2.collision == true) {
			return true;
		}
		
		
	return false;
}

	/**
	 * Addiert alle Kraftvektoren zu einer Gesamtkraft zusammen
	 */
	private void berechneGesamtKraft() {
	if(kraefte!= null) {
		gesamtKraft.setZero(); //GesamtKraft neu berechnen
		for (Map.Entry<String,Vector2D> entry : kraefte.entrySet()) { //Update alle Komponenten
			gesamtKraft.add(entry.getValue()); //Gesamtkraft aufaddierens
	    }
	}
	}
	


	/**
	 * Funktion die in Jedem Schleifendurchgang die Kraftvektoren abbaut
	 */
	private void RichtungsKraftVektorenAbbauen() {
		//Kraftminimierung //Funktion
		if(kraefte != null) {
			for (Map.Entry<String, Vector2D> entry : kraefte.entrySet()) { //Update alle Komponenten
				if(entry.getValue().x != 0) {
					if(entry.getValue().x > 0) {
						entry.getValue().x -= fahrKraftsteigerung/2;
					}
					if(entry.getValue().x < 0) {
						entry.getValue().x += fahrKraftsteigerung/2;
					}
					
		    }
				
				if(entry.getValue().y != 0) {
					if(entry.getValue().y > 0) {
						entry.getValue().y-= fahrKraftsteigerung/2;
					}
					if(entry.getValue().y < 0) {
						entry.getValue().y+= fahrKraftsteigerung/2;
					}
					
				}
			}
			
		}
		
	}

	@Override
	public int GetKomponentId() {
		return komponentId;
	}
	

	@Override
	/**
	 * Erhalte Ergnisse die Kollsionskraftvektoren addieren
	 */
	public void updateKomponentenEvent(String eventType, Object... eventData) {
		if(eventType == "key_event") {
	if(optionen.status == Spielstadien.Status_Spiel_Laeuft) {
		up = (boolean) eventData[1];
		down = (boolean) eventData[2];
		left = (boolean) eventData[3];
		right = (boolean) eventData[4];
		}
		}		
		if(eventType == "add_collision_force") {
			addKraft("Kollision_Kraft", new Vector2D((Double) eventData[1], (Double) eventData[2]));
		}
		
	}


}
