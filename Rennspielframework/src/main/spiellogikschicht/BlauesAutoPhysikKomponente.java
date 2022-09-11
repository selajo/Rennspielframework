package spiellogikschicht;

/**
 * Die PhysikKomponente eines BlauenAutos
 * @author André
 *
 */
public class BlauesAutoPhysikKomponente extends APhysikKomponente{
	
	/**
	 * Name der Komponente
	 */
	public String name = "BlauesAutoPhysikKomponente";

	@Override
	public String VGetName() {
		return name;
	}

	@Override
	public int getIdVonName(String name) {
		if(this.name == name) {
			return komponentId;
		}
		return 0;
	}

}