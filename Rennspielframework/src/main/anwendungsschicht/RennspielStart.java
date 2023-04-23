package anwendungsschicht;
import org.apache.commons.cli.ParseException;
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
		try {
			CLI.initOptions(args);
			if(!CLI.checkIfOk()) {
				throw new ParseException("Einige Informationen fehlen noch");
			}
		} catch (ParseException e) {
			logger.error("Ein Fehler ist beim Parsen der Eingabe aufgetreten");
			CLI.printHelp();
			return;
		}

		Rennspiel_SpielApplikation app = new Rennspiel_SpielApplikation();			//Ein Neues Spiel initialisieren
		logger.info("RennSpielStart.main - starting");
		app.start();																//Startet den Thread fuer ein Neues Spiel
		logger.info("RennSpielStart.main - ending");
	}
}
