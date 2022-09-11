package anwendungsschicht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse organisiert die Kommunikation zwischen den Systen.
 * @author André
 * 
 */
public class EventManager { //https://refactoring.guru/design-patterns/observer/java/example
	
	/**
	 * Enthaelt die Instance des Klasse (siehe Methode getInstance) teil des Singelton-Pattern
	 */
	 private static EventManager INSTANCE; //Neuer Singelton, Der EventManager muss ein Singelton sein.
	
	 /**
	  * Sind alle Zuhoerer die auf einkommende Ereignisse warten. Sortiert in einer Hash-Map mit dem Ereignistyp als Schluessel und eine Liste von allen 
	  * Klassen die dieses Ereignis empfangen moechten.
	  */
	 Map<String, List<EventListener>> listeners = new HashMap<>(); //HashMap wo alle Eventtypen und EventListener gespeichert sind
	 	
	 /**
	  * Konstruktor, der die Hash-Map mit Ereignistypen fuellt
	  * @param operations Ereignistypen
	  */
	    public EventManager(String... operations) {
	        for (String operation : operations) {
	            this.listeners.put(operation, new ArrayList<>());
	        }
	    }
	    
	    /**
	     * Konstrutor ohne Initialisierung
	     */
	    public EventManager() {}
	    
	    
	    /**
	     * Funktion die neue Listener der Hash Map hinzufuegt
	     * @param eventType Ereignistyp
	     * @param listener	ListenerKlasse
	     */
	    public void subscribe(String eventType, EventListener listener) {
	    	
	    	if(!listeners.containsKey(eventType)) {
	    		listeners.put(eventType, new ArrayList<>());
	    	}   	
	        List<EventListener> users = listeners.get(eventType);
	        users.add(listener);
	        //System.out.println("added "+ eventType);
	    }
	    
	    /**
	     * Methode die einen Listener aus der Hash-Map wieder entfernt
	     * @param eventType Ereignistyp
	     * @param listener	ListenerKlasse
	     */
	    public void unsubscribe(String eventType, EventListener listener) {
	        List<EventListener> users = listeners.get(eventType);
	        users.remove(listener);
	        System.out.println("removed "+ eventType);
	    }
	    
	    /**
	     * Methode die alle fuer den Ereignistypen registierten Listener ueber ein neues Ereignis informiert
	     * @param eventType Ereignistyp
	     * @param eventData Ereignisdaten
	     */
	    public void notify(String eventType, Object... eventData) {
	        List<EventListener> users = listeners.get(eventType);
	        if(users != null) {
	        for (EventListener listener : users) {
	        	//System.out.print("Event");
	            listener.updateEvent(eventType, eventData);
	        }
	        }
	    }
	    
	    /**
	     * Die getInstanz zum Singelton
	     * @return aktuelle Instanz des EreignisManager
	     */
	    public static EventManager getInstance() { 
	        if(INSTANCE == null) {
	            INSTANCE = new EventManager();
	        }
	        
	        return INSTANCE;
	    }
}
