package anwendungsschicht;



/**
 * Interface.
 * Klassen die dieses Interface implemntieren, koennen Nachrichten vom EventManager erhalten
 * @author André
 *	
 */
public interface EventListener {
	/**
	 * Ereignisse koennen in den Zielklassen realisiert werden
	 * @param eventType Zeichenkette die den Ereignistypen angibt
	 * @param eventData Ereignisdaten als Object[]
	 */
	void updateEvent(String eventType, Object... eventData);
}
