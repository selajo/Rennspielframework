package kiansichtsschicht;

import org.junit.Test;
import anwendungsschicht.Spieloptionen;
import anwendungsschicht.TileKoordinate;
import kiansichtsschicht.SpielGraph;
import kiansichtsschicht.SpielKnoten;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;

/**
 * Unit-Tests für kiansichtsschicht.SpielGraph
 * @author josef
 *
 */
public class SpielGraphTest {
	@Test
	public void test_Pruefe_Knoten_Reibung() {
		Spieloptionen optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen("/Res/Config/ConfigWorld01.json");
		
		SpielGraph graph = new SpielGraph();

		SpielKnoten[][] alleKnoten = graph.getAlleSpielKnoten();
		int mapTileNum[][] = optionen.mapTileNum;
		
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				Object[] obj = optionen.tileInformationen.get(mapTileNum[j][i]);
				assertEquals(alleKnoten[i][j].getReibung(), obj[2]);
			}
		}
	}
	
	@Test
	public void test_Pruefe_Knoten_Kollision() {
		Spieloptionen optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen("/Res/Config/ConfigWorld01.json");
		
		SpielGraph graph = new SpielGraph();

		SpielKnoten[][] alleKnoten = graph.getAlleSpielKnoten();
		int mapTileNum[][] = optionen.mapTileNum;
		
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				Object[] obj = optionen.tileInformationen.get(mapTileNum[j][i]);
				assertEquals(alleKnoten[i][j].getKollision(), obj[3]);
			}
		}
	}
	
	@Test
	public void test_Pruefe_Knoten_Ziel() {
		Spieloptionen optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen("/Res/Config/ConfigWorld01.json");
		
		SpielGraph graph = new SpielGraph();

		SpielKnoten[][] alleKnoten = graph.getAlleSpielKnoten();
		int mapTileNum[][] = optionen.mapTileNum;
		
		for(int i = 0; i < optionen.maxBildschirmZeilen; i++ ) {
			for(int j = 0; j < optionen.maxBildschirmSpalten; j++) {
				Object[] obj = optionen.tileInformationen.get(mapTileNum[j][i]);
				assertEquals(alleKnoten[i][j].getZiel(), obj[4]);
			}
		}
	}

	/*
	@Test
	public void test_Pruefe_Knoten_Checkpoints() {
		Spieloptionen optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen("/Res/Config/ConfigWorld01.json");
		
		SpielGraph graph = new SpielGraph();

		SpielKnoten[][] alleKnoten = graph.getAlleSpielKnoten();
		//int mapTileNum[][] = optionen.mapTileNum;
		
		Map<Integer, List<TileKoordinate>> checkpoints = optionen.checkpointListe;
		for( List<TileKoordinate> obj : checkpoints.values()) {
			for (int i = 0; i < obj.size(); i++) {
				assertEquals(alleKnoten[obj.get(i).getTileY()-1][obj.get(i).getTileX()-1].getCheckpoint(), true);
			}
		}


	}

	 */

}
