package spiellogikschicht;

/**
 * DatenKompoenten fuer ein Blaues Auto
 * @author André
 *
 */
public class BlauesAutoAkteurDatenKomponente extends AKomponenteAkteurDaten{
	/**
	 * Name der Komponente
	 */
	public final String name = "BlauesAutoDatenKomponente";
		
	
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