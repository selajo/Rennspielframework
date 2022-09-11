package spielansichtsschicht;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;

/**
 * Klasse die ein gelbes Fahrzeug repraesentiert mir seinen grafischen Anzeigedaten
 * @author André
 *
 */
public class GelbesAuto  extends Entity {
	EventManager event; 
	Controller controller;
	
	/**
	 * Konstruktor, initialisert das Auto mit den grafischen Daten und holt die AutoBilder aus den Spieloptionen
	 * @param controller
	 */
	public GelbesAuto(Controller controller) { //Konstruktor
		this.controller = controller;
		setDefaultValues();				//DefaultWerteSetzten
		getAutoImage();
	}

	@Override
	/**
	 * Holen der Bilder von den Spieloptionen
	 */
	public void getAutoImage() {
		Spieloptionen opt = Spieloptionen.getInstance();
		up1 = opt.autoTiles.get(4).get(1);
		down1 = opt.autoTiles.get(4).get(2);
		left1 = opt.autoTiles.get(4).get(3);  
		right1 = opt.autoTiles.get(4).get(4);
	}

	/**
	 * Setzte Defaultwerte falls Probleme bei der Dateninitialiserung gibt
	 */
	private void setDefaultValues() {
		worldX = 3 * controller.spielDaten.tileGroesse;
		worldY = 3 * controller.spielDaten.tileGroesse;
		
		direction = "down";
		
	}

	@Override
	/**
	 * Zeichnen des GelbenAutos
	 */
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
				image = up1;
				break;
		case "down":
			image = down1;
			break;
		case "left":
			image = left1;
			break;
		case "right":
			image = right1;
			break;

		}
		g2.drawImage(image, worldX, worldY, controller.spielDaten.tileGroesse, controller.spielDaten.tileGroesse, null);
		
	}

}