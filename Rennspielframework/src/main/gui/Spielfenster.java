package gui;



import javax.swing.JFrame;
import spielansichtsschicht.Controller;

/**
 * Spielfenster das eine grafische Benutzeroberflaeche startet und dessen Grundeigenschaften darstellt
 * @author André
 *
 */
public class Spielfenster {
	/**
	 * Instanz des Controllers ueber den man Zugriff auf Grafische Daten bekommt
	 */
	private Controller controller;
	private JFrame frame;
	private SpielPanel spielpanel;
	
	/**
	 * Konstruktor der dem Spielfenster einen Controller zuweist und ein Java GUI startet
	 * @param controller Controller
	 */
	public Spielfenster(Controller controller){
		this.controller = controller;
		erschaffeSpielfenster();
		}
		
		
	/**
	 * Erstellt ein neues Spielfenster und weise dem ein SpielPanel zu. Sowie setzte Grundeinstellungen des Spielfenster
	 */
	private void erschaffeSpielfenster() {
		frame = new JFrame(controller.spielDaten.titel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		spielpanel = new SpielPanel(controller);
		frame.add(spielpanel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	

	public JFrame getFrame() {
		return frame;
	}

	public SpielPanel getPanel() {
		return spielpanel;
	}
	
	
	
}
