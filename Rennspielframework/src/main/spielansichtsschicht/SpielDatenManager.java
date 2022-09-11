package spielansichtsschicht;

import anwendungsschicht.Spieloptionen;

/**
 * Klasse, die grundsaeetzliche grafische Spieldaten enthaelt
 * @author André
 *
 */
public class SpielDatenManager { //Besserwarscheinlich ueber Spieloptionen //Singelton
	//Einstellungen
		Spieloptionen optionen;
		public int tileOrginalGroessen = 16; //16x16 Tile
		public int vergroesserung = 3;	
		
		public int tileGroesse = tileOrginalGroessen * vergroesserung;
		public int maxBildschirmSpalten = 30;
		public int maxBildschirmZeilen = 20;
		public int bildschirmHoehe = tileGroesse * maxBildschirmZeilen;
		public int bildschirmBreite = tileGroesse * maxBildschirmSpalten;
		public String world;
		
		public  String titel = "2D Rennspiel Andre Zimmer";
		
		/**
		 * Konstruktor, der sich alle benoetigten grafischen Daten von den Spieloptionen holt
		 */
		public SpielDatenManager() {
			optionen = Spieloptionen.getInstance();
			tileOrginalGroessen = optionen.tileOrginalGroessen;
			vergroesserung = optionen.vergroesserung;
			tileGroesse = optionen.tileGroesse;
			maxBildschirmSpalten = optionen.maxBildschirmSpalten;
			maxBildschirmZeilen = optionen.maxBildschirmZeilen;
			bildschirmHoehe = optionen.bildschirmHoehe;
			bildschirmBreite = optionen.bildschirmBreite;
			titel = optionen.titel;
			world = optionen.spielfeld;
		}
		
		//public final String world = "/Map/worldV2_00.txt"; //String von der Map
}
