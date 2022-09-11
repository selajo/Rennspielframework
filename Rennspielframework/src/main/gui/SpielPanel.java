package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import spielansichtsschicht.Controller;

/**
 * Ein SpielPanel auf dem das Spiel gezeichnet wird
 * @author André
 *
 */
public class SpielPanel extends JPanel {
	
	private Controller controller;
	
	/**
	 * Initialisiere ein SpielPanel, BildschirmGroe�e wird aus den Spieloptionen uebernommen.Zuweisung des TasterturHandlers fuer die Registrierung von Tastatureingaben.
	 * @param controller Controller
	 */
	public SpielPanel(Controller controller) {
		this.controller = controller;
		
		this.setPreferredSize(new Dimension(controller.spielDaten.bildschirmBreite, controller.spielDaten.bildschirmHoehe));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(controller.tastH);  //TastaturHandler aus dem Controller dem SpielPanel zuweisen
		this.setFocusable(true);
		
	}
	
	/**
	 * Render Funktion, zustaendig fuer das Zeichnen der Inhalte auf dem Spielfeld
	 */
	public void Render () {
		repaint();
	}
	
	public void Update() {}
	
	/**
	 * Ueberschreibt Klasse aus JPanel, Zeichnen der Spielobjekte und des Spielfelds
	 */
	public void paintComponent(Graphics g) {	
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		controller.draw(g2); //Zeichnen der Objekte, und Spielfeld
		g2.dispose();
	}
	
}
