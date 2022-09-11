package spiellogikschicht;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;

/**
 * Die Repraesention der Spielfelddaten in der Spiellogik
 * @author André
 *
 */
public class KartenManager {
	/*
	 * Instanz des KartenManager, Singelton Pattern
	 */
	private static KartenManager INSTANCE; //Singelton
	
	//public String aktuellesSpielfeld = "/Map/world01.txt"; //Defautl wert momentan
	public Spieloptionen spieloptionen;
	/**
	 * Array mit allen im Spiel nutzbaren Tiles
	 */
	public Tile[] tile; //Art der Tiles
	/**
	 * Integer[][] Das die Spielkart repraesentiert
	 */
	public int mapTileNum[][];
	/**
	 * Tile[][] Repraesentation des Spielfeld mit den zugeweisenen Tiles an der richtigen Stelle
	 */
	public Tile mapTiles[][];
	
	
	/**
	 * Konstruktor, richtige Initialiserung der Spielfeld Daten
	 */
	public KartenManager() {//Daten wie Colloms und Rows benoetigt man noch irgendwo
		spieloptionen = Spieloptionen.getInstance();
		
		tile = new Tile[200]; //200 unterschiedliche Tiles
		mapTileNum = new int[spieloptionen.maxBildschirmSpalten][spieloptionen.maxBildschirmZeilen];
		mapTiles = new Tile[spieloptionen.maxBildschirmSpalten][spieloptionen.maxBildschirmZeilen];
		
		setArtTile();
		loadMap(spieloptionen.spielfeld); //Vorlaeufig
		setCheckpoints();
	}
	
	/**
	 * Zuweiseung der TileEigenschaften
	 */
	private void setArtTile() { //Lade nur die Eigenschaften der Tiles
		
		Map<Integer,Object[]> tileEigenschaften = spieloptionen.tileInformationen;
		
		for(Map.Entry<Integer, Object[]> entry : tileEigenschaften.entrySet()) {
			int tileNummer = entry.getKey();
			Object[] tileWerte = entry.getValue();
			
			tile[tileNummer] = new Tile();
			tile[tileNummer].name = (String) tileWerte[0];
			tile[tileNummer].collision = (boolean) tileWerte[3];
			tile[tileNummer].Reibung = (double) tileWerte[2];
			tile[tileNummer].Ziel = (boolean) tileWerte[4];
	
		}
		
//				tile[0] =  new Tile();
//				tile[0].name = "Gras";
//				tile[0].collision = false;
//				
//				tile[1] =  new Tile();
//				tile[1].name = "Wand"; 
//				tile[1].collision = true;
//				
//				tile[2] =  new Tile();
//				tile[2].name = "Wasser";
//				tile[2].collision = true;
//				
//				tile[3] =  new Tile();
//				tile[3].name = "Erde";
//				tile[3].collision = false;
//				
//				tile[4] =  new Tile();
//				tile[4].name = "Baum";
//				tile[4].collision = true;
//				
//				tile[5] =  new Tile();
//				tile[5].name = "Sand";
//				tile[5].collision = false;
//				
//				tile[9] =  new Tile();
//				tile[9].name = "Strasse_09";
//				tile[9].collision = false;
//				tile[9].Reibung = 1.0;
//				
//				tile[10] =  new Tile();
//				tile[10].name = "Wand_10";
//				tile[10].collision = true;
//				
//				tile[11] =  new Tile();
//				tile[11].name = "Strasse_11";
//				tile[11].collision = false;
//				tile[11].Reibung = 1.0;
//				
//				tile[12] =  new Tile();
//				tile[12].name = "Strasse_12";
//				tile[12].collision = false;
//				tile[12].Reibung = 0.8;
//				
//				tile[13] =  new Tile();
//				tile[13].name = "Strasse_13";
//				tile[13].collision = false;
//				tile[13].Reibung = 0.8;
//				
//				tile[14] =  new Tile();
//				tile[14].name = "Strasse_14";
//				tile[14].collision = false;
//				tile[14].Reibung = 0.8;
//				
//				tile[15] =  new Tile();
//				tile[15].name = "Strasse_15";
//				tile[15].collision = false;
//				tile[15].Reibung = 0.8;
//				
//				tile[16] =  new Tile();
//				tile[16].name = "Strasse_16";
//				tile[16].collision = false;
//				tile[16].Reibung = 0.8;
//				
//				tile[17] =  new Tile();
//				tile[17].name = "Strasse_17";
//				tile[17].collision = false;
//				tile[17].Reibung = 0.8;
//				
//				tile[18] =  new Tile();
//				tile[18].name = "Strasse_18";
//				tile[18].collision = false;
//				tile[18].Reibung = 0.8;
//				
//				tile[19] =  new Tile();
//				tile[19].name = "Strasse_19";
//				tile[19].collision = false;
//				tile[19].Reibung = 0.8;
//				
//				tile[20] =  new Tile();
//				tile[20].name = "Grass_20";
//				tile[20].collision = false;
//				
//				tile[21] =  new Tile();
//				tile[21].name = "Strasse_21";
//				tile[21].collision = false;
//				tile[21].Reibung = 0.8;
//				
//				tile[22] =  new Tile();
//				tile[22].name = "Strasse_22";
//				tile[22].collision = false;
//				tile[22].Reibung = 0.8;
//				
//				tile[23] =  new Tile();
//				tile[23].name = "Strasse_23";
//				tile[23].collision = false;
//				tile[23].Reibung = 0.8;
//				
//				tile[24] =  new Tile();
//				tile[24].name = "Strasse_24";
//				tile[24].collision = false;
//				tile[24].Reibung = 0.8;
//				
//				tile[30] =  new Tile();
//				tile[30].name = "Wasser_30";
//				tile[30].collision = true;
//				
//				tile[31] =  new Tile();
//				tile[31].name = "Wasser_31";
//				tile[31].collision = true;
//				
//				tile[32] =  new Tile();
//				tile[32].name = "Wasser_32";
//				tile[32].collision = true;
//				
//				tile[33] =  new Tile();
//				tile[33].name = "Wasser_33";
//				tile[33].collision = true;
//				
//				tile[34] =  new Tile();
//				tile[34].name = "Wasser_34";
//				tile[34].collision = true;
//				
//				tile[35] =  new Tile();
//				tile[35].name = "Wasser_35";
//				tile[35].collision = true;
//				
//				tile[36] =  new Tile();
//				tile[36].name = "Wasser_36";
//				tile[36].collision = true;
//				
//				tile[37] =  new Tile();
//				tile[37].name = "Wasser_37";
//				tile[37].collision = true;
//				
//				tile[38] =  new Tile();
//				tile[38].name = "Wasser_38";
//				tile[38].collision = true;
//				
//				tile[39] =  new Tile();
//				tile[39].name = "Wasser_39";
//				tile[39].collision = true;
//				
//				tile[40] =  new Tile();
//				tile[40].name = "Wasser_40";
//				tile[40].collision = true;
//				
//				tile[41] =  new Tile();
//				tile[41].name = "Wasser_41";
//				tile[41].collision = true;
//				
//				tile[42] =  new Tile();
//				tile[42].name = "Wasser_42";
//				tile[42].collision = true;
//				
//				tile[45] =  new Tile();
//				tile[45].name = "Ziel_45";
//				tile[45].collision = false;
//				tile[45].Reibung = 1.0;
//				tile[45].Ziel = true;
//				
//				tile[46] =  new Tile();
//				tile[46].name = "Ziel_46";
//				tile[46].collision = false;
//				tile[46].Reibung = 1.0;
//				tile[46].Ziel = true;
//				
//				tile[47] =  new Tile();
//				tile[47].name = "Ziel_47";
//				tile[47].collision = false;
//				tile[47].Reibung = 1.0;
//				tile[47].Ziel = true;
//		
//		tile[50] = new Tile();
//		tile[50].name ="Asphalt_50";
//		tile[50].collision = false;
//		tile[50].Reibung = 0.5;
//		
//		tile[51] = new Tile();
//		tile[51].name ="Asphalt_51";
//		tile[51].collision = false;
//		tile[51].Reibung = 0.5;
//		
//		tile[52] = new Tile();
//		tile[52].name ="Asphalt_52";
//		tile[52].collision = false;
//		tile[52].Reibung = 0.5;
//		
//		tile[53] = new Tile();
//		tile[53].name ="Asphalt_53";
//		tile[53].collision = false;
//		tile[53].Reibung = 0.5;
//		
//		tile[54] = new Tile();
//		tile[54].name ="Asphalt_54";
//		tile[54].collision = false;
//		tile[54].Reibung = 0.5;
	}

	/**
	 * Setze die Checkpoints, aus der angegebenen Datei
	 */
	private void setCheckpoints() {
		
		Map<Integer, List<TileKoordinate>> checkpointMap = spieloptionen.checkpointListe;
		
		if(checkpointMap!=null) {
			
			for(int checkpointNumber = 1; checkpointNumber <= spieloptionen.maxCheckpoint; checkpointNumber++) {
				
				List<TileKoordinate> checkpointList = checkpointMap.get(checkpointNumber);
				
				for(int i = 0; i < checkpointList.size(); i++) {
					TileKoordinate cord = checkpointList.get(i);
					mapTiles[cord.getTileX()-1][cord.getTileY()-1].Checkpoint = checkpointNumber; //Setzte die Checkpoints auf der Karte
				}
			}
		}
	}
	
	/**
	 * Lade eine karte aus dem Res Ordner von einem filePath
	 * @param filePath
	 */
	public void loadMap(String filePath) { //Lade die virtuelle Spielfeldkarte

		mapTileNum = spieloptionen.mapTileNum;

		int col = 0;
		int row = 0;


		while(col < spieloptionen.maxBildschirmSpalten && row < spieloptionen.maxBildschirmZeilen) {

			while(col < spieloptionen.maxBildschirmSpalten) {

				int num = mapTileNum[col][row];

				//neues Tile definieren
				Tile ntile = new Tile();
				ntile.name = tile[num].name;
				ntile.collision = tile[num].collision;
				ntile.Ziel = tile[num].Ziel;
				ntile.Checkpoint = tile[num].Checkpoint;
				ntile.Reibung = tile[num].Reibung;
				//Tile uebergeben
				mapTiles[col][row] = ntile;	//Fuell Array mit Spezifischen Tiles
				col++;
			}
			if(col == spieloptionen.maxBildschirmSpalten) {
				col = 0;
				row ++;
			}
		}
	}

	
	/**
	 * Singelton Pattern
	 * @return instanz
	 */
	 public static KartenManager getInstance() {
	        if(INSTANCE == null) {
	            INSTANCE = new KartenManager();
	        }
	        
	        return INSTANCE;
	    }

}

