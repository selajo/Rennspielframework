package spiellogikschicht;


/**
 * Die PhysikKomponente eines RotenAutos
 * @author André
 *
 */
public class RotesAutoPhysikKomponente extends APhysikKomponente{
	
	/**
	 * Name der Komponente
	 */
	public String name = "RotesAutoPhysikKomponente";

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
