package kiansichtsschicht;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import anwendungsschicht.Spieloptionen;

public class SpielKnoten implements Comparable {

	/**
	 * x-Kooridnate eines Tiles
	 */
	int tileX;
	/**
	 * y-Kooridnate eines Tiles
	 */
	int tileY;

	/**
	 * Tilenummer: Tile-Beschaffenheit
	 */
	int mapTileNummer;

	/**
	 * Reibung eines Tiles
	 */
	double reibung;
	/**
	 * Kollision
	 */
	boolean collision;
	/**
	 * Zeigt als boolean, ob es sich um den Zielknoten handelt
	 */
	boolean ziel;
	/**
	 * zeigt als boolean, ob es sich um einen Checkpoint handelt
	 */
	boolean checkpoint;
	/**
	 * zeigt als boolean, ob es sich um den Startknoten handelt
	 */
	boolean startPunkt = false;
	/**
	 * ist ein Knoten bereits bearbeitet
	 */
	boolean gesehen;
	/**
	 * ist ein Knoten Element des kürzesten berechneten Pfades
	 */
	boolean kiPfadElement;
	/**
	 * Vorgängnerknoten
	 */
	public SpielKnoten parent;
	/**
	 * ArrayListe aller Nachbarknoten
	 */
	ArrayList<SpielKnoten> nachbarn = null;

	/**
	 * relevant für Graphalgorithmen
	 */
	private double kosten, heuristik, funktion, distanz;
	private boolean valide;

	/**
	 * Ausrichtung eine Knoten zu einem anderen
	 */
	private SpielKnoten.Direction Direction;

	/**
	 * java-enum mit allen möglichen Himmelsrichtungen
	 */
	private enum Direction {
		NULL,
		NORDEN,
		SÜDEN,
		WESTEN,
		OSTEN,
		NORDWEST,
		SÜDOST,
		NORDOST,
		SÜDWEST
	}

	/**
	 * Spieloptionen des Rennspiels
	 */
	Spieloptionen optionen;

	/**
	 * erzeugt einen Spielknoten
	 * @param tileX
	 * @param tileY
	 */
	public SpielKnoten(int tileX, int tileY) {
		optionen = Spieloptionen.getInstance();

		this.tileX = tileX;
		this.tileY = tileY;

		this.mapTileNummer = optionen.mapTileNum[tileX][tileY]; //Hole den Kartentyp

		Object[] o = optionen.tileInformationen.get(mapTileNummer);

		this.reibung = (double) o[2];
		this.collision = (boolean) o[3];
		this.ziel = (boolean) o[4];
		this.checkpoint = false;
		this.valide = true;
		this.Direction = null;

		this.gesehen = false;
		this.parent = null;
		this.distanz = Double.MAX_VALUE;
	}

	/**
	 * finde einen bestimmten Spielknoten
	 * @param x
	 * @param y
	 * @param spielKnoten
	 * @return
	 */
	public SpielKnoten find(int x, int y, ArrayList<SpielKnoten> spielKnoten) {
		for (SpielKnoten sk : spielKnoten) {
			if (sk.tileX == x && sk.tileY == y)
				return sk;
		}
		return null;
	}

	/**
	 * ermittle Nachbarknoten und deren Ausrichtung
	 * @param tiles
	 */
	public void berechneNachbarn(ArrayList tiles) {

		ArrayList<SpielKnoten> Knoten = new ArrayList<>();

		int minX = 0;
		int minY = 0;
		int maxX = optionen.maxBildschirmSpalten - 1;
		int maxY = optionen.maxBildschirmZeilen - 1;

		if (tileX < minX || tileX > maxX || tileY < minY || tileY > maxY){
			// Grenzfälle
			System.out.println("Out of Bounds!");
		}

		if (tileX > minX) {
			SpielKnoten nachbarSpielKnoten = find(tileX - 1, tileY, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.WESTEN);
				Knoten.add(nachbarSpielKnoten); //westen
			}
		}

		if (tileX < maxX) {
				SpielKnoten nachbarSpielKnoten = find(tileX + 1, tileY, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.OSTEN);
				Knoten.add(nachbarSpielKnoten); //osten
			}
		}

		if (tileY > minY) {
			SpielKnoten nachbarSpielKnoten = find(tileX, tileY - 1, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.NORDEN);
				Knoten.add(nachbarSpielKnoten); //norden
			}
		}

		if (tileY < maxY) {
			SpielKnoten nachbarSpielKnoten = find(tileX, tileY + 1, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.SÜDEN);
				Knoten.add(nachbarSpielKnoten); //süden
			}
		}

		if (tileX > minX && tileY > minY) {
			SpielKnoten nachbarSpielKnoten = find(tileX - 1, tileY - 1, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.NORDWEST);
				Knoten.add(nachbarSpielKnoten); //nordwest
			}
		}

		if (tileX < maxX && tileY < maxY) {
			SpielKnoten nachbarSpielKnoten = find(tileX + 1, tileY + 1, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.SÜDOST);
				Knoten.add(nachbarSpielKnoten); //südost
			}
		}

		if (tileX < maxX && tileY > minY) {
			SpielKnoten nachbarSpielKnoten = find(tileX + 1, tileY - 1, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.NORDOST);
				Knoten.add(nachbarSpielKnoten); //nordost
			}
		}

		if (tileX > minY && tileY < maxY) {
			SpielKnoten nachbarSpielKnoten = find(tileX - 1, tileY + 1, tiles);
			if (nachbarSpielKnoten != null) {
				nachbarSpielKnoten.setDirection(Direction.SÜDWEST);
				Knoten.add(nachbarSpielKnoten); //southwest
			}
		}
		setNachbarKnoten(Knoten);
	}

	public void setNachbarKnoten(ArrayList<SpielKnoten> nachbarn) {
		this.nachbarn = nachbarn;
	}

	/**
	 * Distanzfunktion der Graphalgorithmen
	 * @param dest
	 * @return
	 */
	public double distanzZu(SpielKnoten dest) {
		SpielKnoten d = dest;
		return new Point(tileX, tileY).distance(new Point(d.tileX, d.tileY));
	}

	/**
	 * Heuristik von A*
	 * @param dest
	 * @return
	 */
	public double heuristik(SpielKnoten dest) {
		return distanzZu(dest);
	}

	/**
	 * dijkstras züruckgehen zum Startknoten vom Zielknoten aus
	 * @return
	 */
	public double berechneEntfernungRekursiv() {
		double entfernung = 0;
		if (this.getParent() != null) {
			//System.out.println("THIS.PARENT" + this.getParent());
			entfernung += parent.distanzZu(this);

			double entfernungVorgaenger = parent.berechneEntfernungRekursiv();
			// Falls momentan keine Route bis zum Startpunkt existiert, wird -1 zurückgegeben
			if (entfernungVorgaenger != -1) {
				entfernung += entfernungVorgaenger;
			} else {
				entfernung = entfernungVorgaenger;
			}

		} else if (!startPunkt) {
			// Kein Vorgänger und kein Startpunkt
			entfernung = -1;
		} else {
			// Es handelt sich um den Startpunkt
			entfernung = 0;
		}
		// System.out.println(entfernung);
		return entfernung;
	}

	@Override
	public int compareTo(Object o) {
		SpielKnoten k = (SpielKnoten) o;
		double differenz = this.distanz - k.distanzZu(this);
		if (differenz == 0) {
			return 0;
		} else if (differenz > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Nachfolgend noch getter und setter zu diversen member
	 */

	public ArrayList<SpielKnoten> getNachbarKnoten() {
		return nachbarn;
	}

	public String toString() {
		return "(" + tileX + ", " + tileY + ", " + reibung + ")";
	}

	public double getReibung() {
		return this.reibung;
	}

	public boolean getKollision() {
		return this.collision;
	}

	public boolean getZiel() {
		return this.ziel;
	}

	public boolean getCheckpoint() {
		return this.checkpoint;
	}

	public SpielKnoten getParent() {
		return parent;
	}

	public void setParent(SpielKnoten parent) {
		this.parent = parent;
	}

	public double getKosten() {
		return kosten;
	}

	public void setKosten(double kosten) {
		this.kosten = kosten;
	}

	public double getHeuristik() {
		return heuristik;
	}

	public void setHeuristik(double heuristik) {
		this.heuristik = heuristik;
	}

	public double getFunktion() {
		return funktion;
	}

	public void setFunktion(double funktion) {
		this.funktion = funktion;
	}

	public void setDistanz(double distanz) {this.distanz = distanz;}

	public double getDistanz(){return distanz;}

	public boolean isValide() {
		return valide;
	}

	public void setValide(boolean valide) {
		this.valide = valide;

	}
	public void setDirection(Direction direction){
		this.Direction = direction;
	}

	public String getDirection() {
		return Direction.name();
	}

}