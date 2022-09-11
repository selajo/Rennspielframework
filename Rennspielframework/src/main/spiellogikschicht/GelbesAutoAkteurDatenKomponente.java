package spiellogikschicht;

/**
 * DatenKompoenten fuer ein Gelbes Auto
 * @author André
 *
 */
public class GelbesAutoAkteurDatenKomponente extends AKomponenteAkteurDaten{
	
	/**
	 * Name der Komponente
	 */
	public final String name = "GelbesAutoDatenKomponente";
	
	
	@Override
	public void Update() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String VGetName() {
		return name;
	}

	@Override
	public int getIdVonName(String name) {
		if(this.name == name) {
			return komponentId;
		}
		return -1; //Default Wert
	}
}
