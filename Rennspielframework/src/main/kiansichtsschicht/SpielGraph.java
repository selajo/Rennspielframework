package kiansichtsschicht;

import java.util.*;

import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;

public class SpielGraph {

	protected int n;
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";


	Spieloptionen optionen;

	KISpielObjekteManager manager;

	SpielKnoten [][] alleSpielKnoten = null;

	ArrayList<SpielKnoten> graph = null;

	Map<Integer, List<TileKoordinate>> checkPoints = null;


	public SpielGraph(){
		optionen  = Spieloptionen.getInstance();
		//int mapTileNum [][] = optionen.mapTileNum;
		alleSpielKnoten = new SpielKnoten[optionen.maxBildschirmZeilen][optionen.maxBildschirmSpalten];

		//Hole alle Checkpoints
		//checkPoints = optionen.checkpointListe;

		//Setze alle SpielKnoten
		setKnoten(); // 600 --> Asphalt wären 200

		//Setze alle Checkpoints
		//setCheckpoints(checkPoints);
	}




	public void initNachbarn(){
		//ArrayList<SpielKnoten> asphaltKnoten = getAsphaltKnoten();
		ArrayList<SpielKnoten> alleSpielKnoten = getAlleSpielKnotenAlsArrayList();
		//for (SpielKnoten sk : asphaltKnoten){
		for (SpielKnoten sk : alleSpielKnoten){
				sk.berechneNachbarn(getAlleSpielKnotenAlsArrayList());
			}
		}


	void setKnoten() {
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				alleSpielKnoten[i][j] = new SpielKnoten(j, i);
			}
		}
	}

	void setCheckpoints(Map<Integer, List<TileKoordinate>> checkpoints) {
		for( List<TileKoordinate> obj : checkpoints.values()) {
			for (int i = 0; i < obj.size(); i++) {
				alleSpielKnoten[obj.get(i).getTileY()-1][obj.get(i).getTileX()-1].checkpoint = true;
			}
		}
	}


	/**
	 * Methode die Weltkoordinaten in TileKoordinaten umwandelt??
	 * @param
	 * @param
	 */
	public TileKoordinate getAktuellePosition(){
		return manager.getTileKoordinate(manager.posX, manager.posY);
	}

	/**
	 * Gibt den Graphen über die Konsole aus.
	 */
	public void printGraph(ArrayList<SpielKnoten> kiPfad) {
		SpielKnoten [][] spielKnotenArray = getAlleSpielKnoten();
		String XundYString = "";
		System.out.println(kiPfad);
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
					//System.out.print(SpielGraph.ANSI_YELLOW_BACKGROUND + spielKnotenArray[i][j].toString() + SpielGraph.ANSI_RESET);
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

	public void kiPfadElementCheck(SpielKnoten spielKnoten, ArrayList<SpielKnoten> kiPfad){
		if (kiPfad.contains(spielKnoten)) {
			spielKnoten.kiPfadElement = true;
		}else{
			spielKnoten.kiPfadElement = false;
		}
	}

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


	public SpielKnoten[][] getAlleSpielKnoten() {
		return this.alleSpielKnoten;
	}

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
