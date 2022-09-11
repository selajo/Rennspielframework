package spielansichtsschicht;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Entity Klasse gibt allen Spielobjekten die grafischen grund Attribute
 * @author André
 *
 */
public abstract class Entity {
	/**
	 * Angabe derSpielerId zu dem das Spielobjekt gehoert
	 */
	public int id; //SpielerID
	/**
	 * Name Autotyp
	 */
	public String name;			//Name Automodell
	/**
	 * Koordinaten des Spielobjekts auf dem Spielfeld
	 */
	public int worldX, worldY;	//Koordinaten in der welt
	
	/**
	 * Bilder die das Spielobjekt repraesentieren
	 */
	public BufferedImage up1,  down1,  left1,  right1; //Bilder des Autos in den verschiedenen Richtungen
	/**
	 * Ausrichtung des Spielobjekt auf dem Spielfeld
	 */
	public String direction; //Richtung des Autos
	
	/**
	 * Abstrakte Methode, in der bestimmt werden muss, wo die Bilder des Autos geholt/eingelesen werden
	 */
	public abstract void getAutoImage();
	/**
	 * Abstrakte Klasse in das Fahrzeug auf ein JavaPanel zeichnet
	 * @param g2 Graphics2D
	 */
	public abstract void draw(Graphics2D g2);
}
