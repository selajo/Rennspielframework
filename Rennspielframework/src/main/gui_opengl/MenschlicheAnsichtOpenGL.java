package gui_opengl;

import anwendungsschicht.Spieloptionen;
import spielansichtsschicht.ISpielAnsicht;
import spielansichtsschicht.SpielAnsichtTyp;

/**
 * Ansicht für die menschliche Ansicht
 */
public class MenschlicheAnsichtOpenGL implements ISpielAnsicht {

    Window window;

    /**
     * AnsichtsID, Identifikaiton der Ansicht
     */
    protected int AnsichtID;
    /**
     * ID des Spielers dem diese Ansicht zugewiesen wurde
     */
    protected int SpielerID;


    public MenschlicheAnsichtOpenGL (){

        window = Window.get();
        window.Init(this);
        //window.run();

    }

    @Override
    /**
     * Gebe zurueck das es sich um eine menschliche Spielansicht handelt
     */
    public SpielAnsichtTyp VGetType() {
        return SpielAnsichtTyp.SpielAnsicht_Mensch;
    }

    @Override
    /**
     * Festlegung das die menschliche Ansicht die ID 1 hat
     */
    public int VGetID() { //Menschliche_Ansicht hat ID 1
        return 1;
    }

    @Override
    /**
     * Weise die Spielansicht einem Spieler zu
     */
    public void AngefuegterSpieler(int vid, int spid) {
        AnsichtID = vid;
        SpielerID = spid;
    }


    @Override
    /**
     * uebertragen des Zeichenimpulses an das SpielPanel
     */
    public void Render(double fTime, float fvergangeZeit) { //Aufgerufen, um die Ansicht darzustellen


        /*spielFenster.getPanel().Render();*/
    }

    @Override
    /**
     * Weitergabe des Updateimpulses an das SpielPanel
     */
    public void Update(float deltaMs) {
        window.update(deltaMs);
       /* spielFenster.getPanel().Update();*/

    }

}
