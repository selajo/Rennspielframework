package kiansichtsschicht;

import java.util.*;

import anwendungsschicht.Spieloptionen;

public class SpielGraph {

	/**
	 * Farben zum Einfärben der KI-Pfade
	 */

	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";


	Spieloptionen optionen;

	KISpielObjekteManager manager;

	/**
	 * 2-d integerArray mit allen Spielknoten
	 */
	SpielKnoten [][] alleSpielKnoten = null;


	/**
	 * Erzeugt den Spielgraphen und initialisiert alle Knoten
	 */
	public SpielGraph(){
		optionen  = Spieloptionen.getInstance();
		alleSpielKnoten = new SpielKnoten[optionen.maxBildschirmZeilen][optionen.maxBildschirmSpalten];
		setKnoten();
	}

	/**
	 * erzeugt alle Knoten; aufgerufen im Konstruktor
	 */
	void setKnoten() {
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				alleSpielKnoten[i][j] = new SpielKnoten(j, i);
			}
		}
	}

	/**
	 * Gibt den Graphen über die Konsole aus.
	 */
	public void printGraph(ArrayList<SpielKnoten> kiPfad) {
		SpielKnoten [][] spielKnotenArray = getAlleSpielKnoten();
		String XundYString = "";
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				kiPfadElementCheck(spielKnotenArray[i][j], kiPfad);
				// 0er vorne anfügen damit Konsolenausgabe Quadratisch wird
				if (i < 10 && j < 10) {
					XundYString = "[" + "0" + i + "|" + "0" + j + "]";
				}else if(j < 10) {
					XundYString = "[" + i + "|" + "0" + j + "]";
				}else if (i < 10){
					XundYString = "[" + "0" + i + "|" + j + "]";
				}else{
					XundYString = "[" + i + "|" + j + "]";
				}

				if(spielKnotenArray[i][j].checkpoint) {
					System.out.print(SpielGraph.ANSI_YELLOW_BACKGROUND + XundYString + SpielGraph.ANSI_RESET);
				}else if(spielKnotenArray[i][j].kiPfadElement){
					System.out.print(SpielGraph.ANSI_RED_BACKGROUND + XundYString + SpielGraph.ANSI_RESET);
				}
				else {
					System.out.print(XundYString);
				}
			}
			System.out.println("\n");
		}
	}

	/**
	 * überprüft ob Spielknoten auf ki-ermittelten Pfad liegt
	 * @param spielKnoten
	 * @param kiPfad
	 */
	public void kiPfadElementCheck(SpielKnoten spielKnoten, ArrayList<SpielKnoten> kiPfad){
		if (kiPfad.contains(spielKnoten)) {
			spielKnoten.kiPfadElement = true;
		}else{
			spielKnoten.kiPfadElement = false;
		}
	}

	/**
	 * gibt alle Knoten die Asphaltknoten sind als Arrayliste zurück
	 * @return
	 */
	public ArrayList<SpielKnoten> getAsphaltKnoten(){
		ArrayList<SpielKnoten> asphaltKnoten = new ArrayList<>();
		SpielKnoten[][] alleKnoten = getAlleSpielKnoten();

		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				if (alleKnoten[i][j].mapTileNummer == 11  || alleKnoten[i][j].mapTileNummer == 9 || alleKnoten[i][j].mapTileNummer == 45 ){
					asphaltKnoten.add(alleKnoten[i][j]);
					}
				}
			}
		return asphaltKnoten;
	}

	/**
	 * hole alle Spielknoten als 2-d integer-array
	 * @return
	 */
	public SpielKnoten[][] getAlleSpielKnoten() {
		return this.alleSpielKnoten;
	}

	/**
	 * hole alle Spielknoten als Arrayliste
	 * @return
	 */
	public ArrayList<SpielKnoten> getAlleSpielKnotenAlsArrayList(){
		ArrayList<SpielKnoten> alleSpielKnotenArray = new ArrayList<>();
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
					alleSpielKnotenArray.add(alleSpielKnoten[i][j]);
			}
		}
		return alleSpielKnotenArray;
	}


}
