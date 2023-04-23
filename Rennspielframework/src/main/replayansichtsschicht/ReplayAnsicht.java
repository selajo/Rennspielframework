package replayansichtsschicht;

import org.json.simple.parser.ParseException;
import spielansichtsschicht.ISpielAnsicht;
import spielansichtsschicht.SpielAnsichtTyp;

import java.io.IOException;

/**
 * Stellt die Ansicht der KI-Spieler dar
 */
public class ReplayAnsicht implements ISpielAnsicht {

    /**
     * AnsichtsID, Identifikaiton der Ansicht
     */
    protected int AnsichtID;
    /**
     * ID des Spielers dem diese Ansicht zugewiesen wurde
     */
    protected int SpielerID;

    ReplayAlgorithm replay;

    /**
     * Konstruktor mit mitgelieferten Argumenten
     * @param args Replay Pfad/zu/timestamps.json
     */
    public ReplayAnsicht(String config){
        replay = new ReplayAlgorithm(config);
        //System.out.println("KI-Ansicht wird gestartet");
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
        try {
            replay.update();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
