package spiellogikschicht;

/**
 * Die PhysikKomponente eines GruenenAutos
 * @author André
 *
 */
public class GruenesAutoPhysikKomponente extends APhysikKomponente{
	
	/**
	 * Name der Komponente
	 */
	public String name = "GruenesAutoPhysikKomponente";

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