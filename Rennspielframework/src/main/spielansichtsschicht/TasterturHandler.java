package spielansichtsschicht;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import anwendungsschicht.EventManager;
import spiellogikschicht.Spielstadien;

/**
 * 
 * @author André
 *Klasse die Tastertureingaben des Spieler feststellt und das Ereignis an die Spiel-Logik sendet
 *TasterturHandler ist nur aktiv wenn das Spiel laeuft
 */
public class TasterturHandler implements KeyListener {
	
//	private static final Spielstadien Status_Spiel_Laeuft = null;
	EventManager event;
	Controller controller;
	
	/**
	 * Speichere die Instanz des Eventhandlers
	 */
	public TasterturHandler() {
		event = EventManager.getInstance();
	}
	public void addController(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Varaiblen die den Tastaturdruck repraesentieren
	 */
	public boolean upPressed, downPressed, leftPressed, rightPressed;

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	/**
	 * Setzte die variablen auf wahr wenn eine Taste gedrueckt wurde und sende das Ereignis an die Spiellogik
	 */
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = true;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if(controller.ansicht.status == Spielstadien.Status_Spiel_Laeuft) {
		event.notify("key_event", controller.ansicht.SpielerID, upPressed, downPressed, leftPressed, rightPressed);
		}
	}

	@Override
	/*
	 * Setzte die Varaiblen auf falsch wenn eine Taste losgelassen wurde und sende das Ereignis an die Spiellogik
	 */
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		if(controller.ansicht.status == Spielstadien.Status_Spiel_Laeuft) {
		event.notify("key_event", controller.ansicht.SpielerID, upPressed, downPressed, leftPressed, rightPressed);
		}
	}

}
