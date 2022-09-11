package kiansichtsschicht;

import org.junit.Test;

import anwendungsschicht.Spieloptionen;
import kiansichtsschicht.SpielKnoten;

import static org.junit.Assert.*;


public class SpielKnotenTest {
	@Test
	public void test_Konstruktor() {
		Spieloptionen optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen("/Res/Config/ConfigWorld01.json");
		SpielKnoten knoten = new SpielKnoten(1, 2);
		
		assertFalse(knoten.getZiel());
	}
}