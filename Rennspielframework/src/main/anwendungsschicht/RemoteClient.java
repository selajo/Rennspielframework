package anwendungsschicht;

import java.net.Socket;

/**Ein RemoteClient repraesentiert einen Clientverbindung zum Server, besitzt die Client ID zur Identifikation
 *  und eine Socket fuer die Kommunikation
 * 
 */
public class RemoteClient {
  private int id;
  private Socket socket;

  /**
   * Erschafft einen Remote Client der die technischen Daten fuer die Kommunikation enthaelt
   * 
   * @param invalid KlientID integer 
   * @param socket Die Socket
   */
  public RemoteClient(int invalid, Socket socket) {
    this.id = invalid;
    this.socket = socket;
  }


/**
 * Bekomme Spieler ID
 * @return Spieler ID
 */
  public int getId() {
    return id;
  }

  /**
   * Bekomme Client Socket
   * @return socket
   */
  public Socket getSocket() {
    return socket;
  }
  
  /**
   * Weise einem Client eine neue Socket zu
   * @param socket
   */
  public void setSocke(Socket socket){
	  this.socket = socket;
  }

 
  @Override
  public String toString() {
    return "[RemoteClient: " + id + " + socket.getRemoteSocketAddress() + ]";
  }
}

