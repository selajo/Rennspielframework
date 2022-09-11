package spielansichtsschicht;

/**
 * Interface das die Grundfunktionen einer Spielansicht definiert
 * @author André
 *
 */
public interface ISpielAnsicht {
	/**
	 * Impuls zum neu Zeichnen einer grafischen Oberflaeche
	 * @param fTime
	 * @param fvergangeZeit
	 */
	public void Render(double fTime, float fvergangeZeit);
	/**
	 * Festlegen, welcher Spielansichtstyp implemntiert wurde
	 * @return Spielansichtstyp
	 */
	public SpielAnsichtTyp VGetType();
	/**
	 * Erhalte die ID des Spielers fuer den die Ansicht geschaffen wurde
	 * @return
	 */
	public int VGetID();
	/**
	 * Fuege einen Spieler der Ansicht an.
	 * @param vid AnsichtsId
	 * @param spid	SpielerID(-1 fuer eine Ansicht ohne Spieler(z.B. auf dem Server))
	 */
	public void AngefuegterSpieler(int vid, int spid); //Funktion die eine Ansicht an einen Spieler bindet Parameter(viewID, AkteurID)
	/**
	 * Updatefunktion, update Moeglichkeit die Spielansicht upzudaten mit der Spielhautpschleife
	 * @param fTime
	 */
	public void Update(float fTime);
}
