package anwendungsschicht;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasse die dafuer zustaendig ist das Spiel zu starten.
 * @author André
 * 
 */
public class RennspielStart {
	/**
	 * Eintrittspunkt in das Spiel
	 * @param args	mitgegebene Spielparameter
	 */

	private static final Logger logger = LogManager.getLogger(RennspielStart.class);

	public static void main (String[] args) {

		Rennspiel_SpielApplikation app = new Rennspiel_SpielApplikation(args);			//Ein Neues Spiel initialisieren
		logger.info("RennSpielStart.main - starting");
		app.start();																//Startet den Thread fuer ein Neues Spiel
		logger.info("RennSpielStart.main - ending");
	}
}
