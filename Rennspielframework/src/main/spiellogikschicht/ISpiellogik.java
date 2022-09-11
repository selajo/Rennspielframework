package spiellogikschicht;

/**
 * 
 * @author André
 *Dieses Interface repraesentiert die Grundfunktionen aller Spiellogik
 *
 *
 */
public interface ISpiellogik {
	 /**
	  * 
	  * @param ID SpielerID
	  * @param spielerresource Autotyp
	  * @return Spielakteur
	  */
	public Spielakteur VErschaffeSpieler(int ID, String spielerresource); //Ein Spielakteur wird erschaffen indem einen String auf den Pfad der XML Resource uebergeben wird
	/**
	 * Update der Spiellogikschicht
	 * @param fTime
	 */
	public void VUpdate(float fTime);
	/**
	 * aendere den Status des Spiels
	 * @param neuerStatus
	 */
	public void VAendereStatus(Spielstadien neuerStatus);

	
}
