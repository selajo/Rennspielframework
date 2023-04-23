package spielansichtsschicht;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import anwendungsschicht.Spieloptionen;
import spiellogikschicht.Spielstadien;
/**
 * Klasse, die grafische Spieldaten verwaltet, wie die grafischen Informatioenen ueber alle Spielobjekte und Spielfeld 
 * @author André
 *
 */
public class Controller {
	public MenschlicheAnsicht ansicht;
	public SpielObjekteManager spielObjektManager; 
	public SpielFeldManager spielfeld; 				//Manager, der die grafischen Spielfeld/Karten Daten beinhaltet.
	public SpielDatenManager spielDaten;
	public TasterturHandler tastH;
	
	/**
	 * Konstruktor Controller, Zuweisung aller Klassen die sich um die grafische Datenverwaltung beinhalten
	 * @param ansicht MenschlicheAnsicht zugriff auf die finalen Spielergebnisse
	 * @param spielDaten SpielDatenManager beinhaltet grundlegende Spieldaten
	 * @param spielfeld	SpielFeldManager 
	 * @param spielObjektManager SpielObjektManager verwaltet die grafischen Daten der Spielobjekte
	 * @param tastH TastaturHandler
	 */
	public Controller(MenschlicheAnsicht ansicht, SpielDatenManager spielDaten, SpielFeldManager spielfeld,SpielObjekteManager spielObjektManager,TasterturHandler tastH) {
		this.ansicht = ansicht;
		this.spielDaten = spielDaten;
		this.spielfeld = spielfeld;
		this.spielObjektManager = spielObjektManager;
		this.tastH = tastH;
		
	}

	
	/**
	 * Zeichnen des Spielinhaltes auf dem Spielfenster, zuerst das Spielfeld, dann die Spielstadien und zum Schluss die Spielers
	 * @param g2 Graphics2D
	 */
	public void draw(Graphics2D g2) {
		//Zeichne das Spielfeld
		spielfeld.draw(g2);
		//zeichnene der Spielstadien
		drawSpielstatus(g2);
		//Zeichnen der Spieler
		spielObjektManager.drawEntities(g2);
		
	}

	/**
	 * Zeichne die Spielstadien, wie Start, Countdown oder Spielerergebnisse auf das Spielfenster
	 * @param g2 Graphics2D
	 */
	private void drawSpielstatus(Graphics2D g2) {
		if (ansicht.status == Spielstadien.Status_WartenAufSpieler) {
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            g2.setColor(Color.WHITE);
            g2.drawString("Warten auf Spieler...", spielDaten.bildschirmBreite/2 -200 , spielDaten.bildschirmHoehe/5);

			String spielmodus = "Aktueller Spielmodus: ";

			Spieloptionen optionen = Spieloptionen.getInstance();
			if(optionen.spielmodus == 1){
				spielmodus += "Der Spieler mit der kürzesten Zeit pro Runde gewinnt";
			}
			else if(optionen.spielmodus == 2) {
				spielmodus += "Der Spieler, welcher zuerst drei vollständige Runden absolviert, gewinnt";
			}

			g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
			g2.setColor(Color.WHITE);
			g2.drawString(spielmodus, spielDaten.bildschirmBreite/2 -200 , spielDaten.bildschirmHoehe/5);
        }   
		else if (ansicht.status == Spielstadien.Status_Spiel_starten) {
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            g2.setColor(Color.WHITE);
            String string = "Spiel startet in: " + ansicht.countdown;
            g2.drawString(string, spielDaten.bildschirmBreite/2 -200, spielDaten.bildschirmHoehe/5);
        } 
		else if (ansicht.status == Spielstadien.Status_Spiel_Beendet) {
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            g2.setColor(Color.WHITE);
            g2.drawString("Spiel vorbei", spielDaten.bildschirmBreite/2-200, spielDaten.bildschirmHoehe/5);

            for(int i = 0; i < ansicht.finaleSpieldaten.length/3; i++) {
            	String string = " Spieler " + ansicht.finaleSpieldaten[3*i] + ": " + ansicht.finaleSpieldaten[3*i+1] + " Runden mit Bestzeit von: " + ansicht.finaleSpieldaten[3*i+2]+  " Sekunden pro Runde";
            	g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));

                 g2.setColor(Color.WHITE);
                 g2.drawString(string, spielDaten.bildschirmBreite/2 -400, spielDaten.bildschirmHoehe/5 +100 + 50*i);
            }
        } 
		
	}
	

}
