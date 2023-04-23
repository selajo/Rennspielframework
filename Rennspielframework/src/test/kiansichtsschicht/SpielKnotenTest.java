package kiansichtsschicht;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import anwendungsschicht.Spieloptionen;
import kiansichtsschicht.SpielKnoten;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


public class SpielKnotenTest {
	@BeforeClass
	public static void setUp() {
		Spieloptionen optionen = Spieloptionen.getInstance();
		optionen.setSpieloptionen("/Res/Config/ConfigWorld01.json");
	}

	@Test
	public void test_Konstruktor() {
		SpielKnoten knoten = new SpielKnoten(1, 2);
		assertFalse(knoten.getZiel());
	}

	@Test
	public void test_find_null() {
		SpielKnoten knoten = new SpielKnoten(1, 2);
		SpielKnoten actual = knoten.find(0, 0, new ArrayList<>(Arrays.asList(knoten)));
		Assert.assertEquals(null, actual);
	}

	@Test
	public void test_find_success() {
		SpielKnoten knoten = new SpielKnoten(1, 2);
		SpielKnoten actual = knoten.find(1, 2, new ArrayList<>(Arrays.asList(knoten)));
		Assert.assertEquals(knoten, actual);
	}

	@Test
	public void test_distanzZu() {
		SpielKnoten knoten = new SpielKnoten(1, 2);
		double actual = knoten.distanzZu(new SpielKnoten(0, 0));
		Assert.assertEquals(2.23, actual, 1);
	}

	@Test
	public void test_heuristik() {
		SpielKnoten knoten = new SpielKnoten(1, 2);
		double actual = knoten.heuristik(new SpielKnoten(0, 0));
		Assert.assertEquals(2.23, actual, 1);
	}

	@Test
	public void test_compareTo() {
		SpielKnoten knoten = new SpielKnoten(1, 2);
		int actual = knoten.compareTo(knoten);
		Assert.assertEquals(1, actual);
	}


}