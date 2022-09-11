package spiellogikschicht;


/**
 * DatenKompoenten fuer ein Rotes Auto
 * @author André
 *
 */
public class RotesAutoAkteurDatenKomponente extends AKomponenteAkteurDaten{
	
	/**
	 * Name der Komponente
	 */
	public final String name = "RotesAutoDatenKomponente";
	
	
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
