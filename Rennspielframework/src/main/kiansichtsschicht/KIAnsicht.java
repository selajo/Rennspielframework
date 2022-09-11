package kiansichtsschicht;

import spielansichtsschicht.ISpielAnsicht;
import spielansichtsschicht.SpielAnsichtTyp;

public class KIAnsicht implements ISpielAnsicht {

	/**
	 * AnsichtsID, Identifikaiton der Ansicht
	 */
	protected int AnsichtID;
	/**
	 * ID des Spielers dem diese Ansicht zugewiesen wurde
	 */
	protected int SpielerID;

	protected AKI kiTyp;

	/**
	 * Konstruktor mit mitgelieferten Argumenten
	 * @param args
	 */
	public KIAnsicht(String [] args){
		kiTyp = KontextKI.ermittleKI(args[4]);

		System.out.println("KI-Ansicht wird gestartet");
	}


	@Override
	public void Render(double fTime, float fvergangeZeit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SpielAnsichtTyp VGetType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int VGetID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void AngefuegterSpieler(int vid, int spid) {
		AnsichtID = vid;
		SpielerID = spid;
	}

	@Override
	public void Update(float fTime) {
		kiTyp.update();
	}

}
