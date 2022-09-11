package anwendungsschicht;

import java.net.Socket;

	/**
	 * Interface um einen Thread zu starten. Fuer die eingegangen Datenpackete
	 * @author André
	 *
	 */
public interface Executable {
	/** @param pack Das empfangene Datenpaket
	   * @param socket Das Socket wo die Nachricht hergekommen ist
	   */
	  public abstract void run(Datapackage pack, Socket socket);

}
