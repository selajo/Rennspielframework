package anwendungsschicht;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Datenpackete die ueber das Netzwerk versendet werden.
 * @author André Zimmer
 *
 */
public class Datapackage {
	/**
	 * Bestimmt die ID des Datenpacketes
	 */
	private int id;
	
	/**
	 * Angabe der Nachrichtlaenge
	 */
	public int datalength = 0;
	
	/**
	 * Die zu uebertragenden Spieldaten ohne Header
	 */
	byte [] data;
	/**
	 * Die zu uebertragenden Spieldaten mit Header
	 */
	byte [] completeMessage;
	
	//Konstruktor
	public Datapackage(){}
	
	/**
	 * Liefert die ID zurueck
	 * @return NachrichtenID
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Liefert die komplette Nachricht mit header
	 * @return completeMessage
	 */
	public byte [] getByteMessage(){
		return completeMessage;
	}
	
	/**
	 * Liest einen BufferedInputStream aus und stetzt die Daten id, datalength, data, completeMessage
	 * @param bis BufferedInputStream
	 */
	public void readByteMessage(BufferedInputStream bis) {
		try {
			//Auslesen NachrichtenID
			byte[] idBytes = bis.readNBytes(4);
			id = convertByteArrayToInt2(idBytes);
			//Auslesen Nachrichtenlaenge
			byte[] lenBytes = bis.readNBytes(4);
			datalength = convertByteArrayToInt2(lenBytes);
			
			//Nachrichtendaten auslesen
			data = bis.readNBytes(datalength);
			
			//Kompination der Ausgelesen Informationen zu der orginalen Nachricht
			byte[] allByteArray = new byte[idBytes.length + lenBytes.length + data.length];
			ByteBuffer buff = ByteBuffer.wrap(allByteArray);
			buff.put(idBytes);
			buff.put(lenBytes);
			buff.put(data);

			completeMessage = buff.array();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Kobination eines Datenpacketes aus den mitgegebenen Parametern
	 * @param id NachrichtenID
	 * @param length Nachrichtenlaenge
	 * @param b Nachrichtendaten als ByteArray
	 */
	public void setDatenDatapackage(int id, int length, byte[] b) {
		this.id = id;
		this.datalength = length;
		this.data = b;
		
		
		byte[] bid = convertIntToByteArray(id);
		byte[] blength = convertIntToByteArray(datalength);
		
		byte[] allByteArray = new byte[bid.length + blength.length + data.length];

		ByteBuffer buff = ByteBuffer.wrap(allByteArray);
		buff.put(bid);
		buff.put(blength);
		buff.put(data);

		completeMessage = buff.array();
	}
	
	/**
	 * Hilfsfunktion die Integes in ByteArrays umwandelt
	 * @param value Integerwert
	 * @return ByteArray[4]
	 */
	public static byte[] convertIntToByteArray(int value) {
		 return new byte[] {
		            (byte)(value >> 24),
		            (byte)(value >> 16),
		            (byte)(value >> 8),
		            (byte)value};
  }
	
	/**
	 * Hilfsfunktion die ByteArrays in Integes umwandelt
	 * @param bytes  ByteArray[4]
	 * @return Integer
	 */
	 public static int convertByteArrayToInt2(byte[] bytes) {
	        return ((bytes[0] & 0xFF) << 24) |
	                ((bytes[1] & 0xFF) << 16) |
	                ((bytes[2] & 0xFF) << 8) |
	                ((bytes[3] & 0xFF) << 0);
	    }
	
}
