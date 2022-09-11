package anwendungsschicht;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Klasse die TileKoordinaten beinhaltet
 * @author André
 *
 */

public class TileKoordinate {
	/**
	 * Die X-Koordinate der Tile.
	 */
	private int tileX = 0;
	/**
	 * Die Y-Koordinate der Tile.
	 */
	private int tileY = 0;

	/**
	 * Erzeugt neue Instanz mit angegebenen Koordinaten.
	 * @param tileX Zu setzende X-Koodinate.
	 * @param tileY Zu setzende Y-Koordinate.
	 */
	public TileKoordinate(int tileX, int tileY) {
		this.tileX = tileX;
		this.tileY = tileY;
	}

	/**
	 * Die Richtung, der aktuellen Tile.
	 */
	public String richtung;

	/**
	 * Erzeugt neue Instanz mit angegebenen Koordinaten inkl. Richtung.
	 * @param tileX Zu setzende X-Koodinate.
	 * @param tileY Zu setzende Y-Koordinate.
	 * @param tileRichtung Zu setzende Fahrtrichtung.
	 */
	public TileKoordinate(int tileX, int tileY, String tileRichtung) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.richtung = tileRichtung;
	}

	/**
	 * Gibt die X-Koordinate zurueck.
	 * @return Die X-Koordinate der aktuellen Instanz.
	 */
	public int getTileX() {
		return tileX;
	}

	/**
	 * Setzt die X-Koordinate.
	 * @param tileX Die zu setzende X-Koordinate.
	 */
	public void setTileX(int tileX) {
		this.tileX = tileX;
	}

	/**
	 * Gibt die Y-Koordinate zurueck.
	 * @return Die Y-Koordinate der aktuellen Instanz.
	 */
	public int getTileY() {
		return tileY;
	}

	/**
	 * Setzt die Y-Koordinate.
	 * @param tileY Die zu setzende Y-Koordinate.
	 */
	public void setTileY(int tileY) {
		this.tileY = tileY;
	}

	/**
	 * Gibt die Tile als String zurueck.
	 * @return Die Tile in String-Form.
	 */
	public String toString() {
		return "X: " + tileX + ", Y: " + tileY;
	}

	/**
	 * Prueft, ob die aktuelle Tile innerhalb der angegebenen Liste ist.
	 * @param list Die zu pruefende Liste.
	 * @return True: Punkt ist in der List; False: andernfalls.
	 */
	public boolean isInArea(List<TileKoordinate> list) {
		for(TileKoordinate l : list) {
			if(l.getTileY() == this.getTileY() && l.getTileX() == this.getTileX()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Erweitert die angegebene Liste um den Bereich an Punkten, die im angegebenen Delta liegen.
	 * @param list Zu erweiternde Liste.
	 * @param delta Hinzuzufuegender Bereichs-Radius.
	 * @return Erweiterte Liste.
	 */
	public List<TileKoordinate> attachToList(List<TileKoordinate> list, int delta) {
		List<TileKoordinate> toAdd = new ArrayList<>();
		for(TileKoordinate l : list) {
			toAdd.add(new TileKoordinate(l.getTileX()+delta, l.getTileY()));
			toAdd.add(new TileKoordinate(l.getTileX()-delta, l.getTileY()));
			toAdd.add(new TileKoordinate(l.getTileX(), l.getTileY()+delta));
			toAdd.add(new TileKoordinate(l.getTileX(), l.getTileY()-delta));
		}

		return Stream.concat(list.stream(), toAdd.stream())
				.collect(Collectors.toList());
	}

	/**
	 * Prueft, ob die aktuelle Tile in der angegebenen Liste inkl. Erweiterungsbereich Delta liegt.
	 * @param list Die zu pruefende Liste.
	 * @param delta Zu pruefender Bereichs-Radius.
	 * @return True: Punkt liegt im Bereich der Liste; False: andernfals.
	 */
	public boolean isInArea(List<TileKoordinate> list, int delta) {
		List<TileKoordinate> extendedList = attachToList(list, delta);
		return isInArea(extendedList);
	}
}

