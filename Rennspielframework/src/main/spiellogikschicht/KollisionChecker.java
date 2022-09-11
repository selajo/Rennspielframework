package spiellogikschicht;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Map;

import anwendungsschicht.EventManager;
import anwendungsschicht.Spieloptionen;

/**
 * Klasse, die Spielobjekte auf Kollisionen ueberprueft. Sie werden sowohl auf Kollsionen mit spielfeldern als auch Kollsionen mit 
 * @author André
 *
 */
public class KollisionChecker {
	
	BasicSpiellogik spiellogik;
	Spieloptionen optionen;
	EventManager event;
	
	public KollisionChecker(BasicSpiellogik spiellogik) {
		this.spiellogik = spiellogik;
		optionen = Spieloptionen.getInstance();
		event = EventManager.getInstance();
	}
	
	/**
	 * Ueberpruefe alle Spielobjekte auf Kollsionen untereinander und Kollsionen mit Tiles
	 */
	public void update() {
		if(spiellogik.akteure!= null) {
			for (Map.Entry<Integer, Spielakteur> entry : spiellogik.akteure.entrySet()) {
				
				int key = entry.getKey();
				checkTileCollisionAndCheckpoints(spiellogik.akteure.get(key));
				
				for (Map.Entry<Integer, Spielakteur> entry1 : spiellogik.akteure.entrySet()) {
					int key1 = entry1.getKey();
					if(key1!=key) {						
							checkAkteurCollision(spiellogik.akteure.get(key),spiellogik.akteure.get(key1));
					}
					
					
				}
				
		    }
		}
	}

	/**
	 * Ueberpruefe auf Kollsionen von zwei Spielakteuren
	 * @param akteur1 Spielakteur
	 * @param akteur2 Spielakteur
	 */
	public void checkAkteurCollision(Spielakteur akteur1, Spielakteur akteur2) {
		//Hole von beiden die Datenkomponente
		AKomponenteAkteurDaten dataAk1 = (AKomponenteAkteurDaten) akteur1.GetKomponente(1);
		AKomponenteAkteurDaten dataAk2 = (AKomponenteAkteurDaten) akteur2.GetKomponente(1);
		
		APhysikKomponente phyAk1 = (APhysikKomponente) akteur1.GetKomponente(2);

		//Get Koordinaten Akteur feste Areale akteur1
		Rectangle recAkt1 = new Rectangle();
		recAkt1.x = ((int) akteur1.position.x) + dataAk1.festeCollisionBox.x;
		recAkt1.y = ((int) akteur1.position.y) + dataAk1.festeCollisionBox.y;
		recAkt1.width = dataAk1.festeCollisionBox.width;
		recAkt1.height = dataAk1.festeCollisionBox.height;
		//Get Koordinaten Akteur festeAreale akteur2
		Rectangle recAkt2 = new Rectangle();
		recAkt2.x = ((int) akteur2.position.x) + dataAk2.festeCollisionBox.x;
		recAkt2.y = ((int) akteur2.position.y) + dataAk2.festeCollisionBox.y;
		recAkt2.width = dataAk2.festeCollisionBox.width;
		recAkt2.height = dataAk2.festeCollisionBox.height;
		
		//KollisionsVariablenZuruecksetzten
		phyAk1.kollisionUp = false;
		phyAk1.kollisionDown = false;
		phyAk1.kollisionLeft = false;
		phyAk1.kollisionRight = false;
		
		//Schneiden sich die Rechtecke
		if(recAkt1.intersects(recAkt2)) {
			
			switch(akteur1.direction) {
			case"up":
				if(recAkt1.y /*-7*/ < recAkt2.y + dataAk2.festeCollisionBox.height && recAkt1.y + dataAk1.festeCollisionBox.height /*-7*/ > recAkt2.y + dataAk2.festeCollisionBox.height ) {
					phyAk1.kollisionUp = true;
					//System.out.println("upcollison");
					double kraft = phyAk1.kraefte.get("upKraft").y/2;
					phyAk1.kraefte.get("upKraft").y = 0;
					event.notify("add_collision_force", akteur1.GetId(),(double) 0 , (double) kraft + 20000 );
					event.notify("add_collision_force", akteur2.GetId(),(double) 0 , (double) -kraft - 20000 );
				}
				break;
			case "down":
				//DownKontrolle
				 if(recAkt1.y + dataAk1.festeCollisionBox.height /*+7*/  > recAkt2.y && recAkt1.y + dataAk1.festeCollisionBox.height /*+7*/  < recAkt2.y + dataAk2.festeCollisionBox.height) {
					 phyAk1.kollisionDown = true;
					//System.out.println("downcollison");
					double kraft = phyAk1.kraefte.get("downKraft").y/2;
					phyAk1.kraefte.get("downKraft").y = 0;
					event.notify("add_collision_force", akteur1.GetId(),(double) 0 , (double) - kraft - 10000);
					event.notify("add_collision_force", akteur2.GetId(),(double) 0 , (double)  kraft + 10000 );
				}
				break;
			case "left":
				//leftKontrolle
				if(recAkt1.x /*-7*/< recAkt2.x + dataAk2.festeCollisionBox.width && recAkt1.x + dataAk1.festeCollisionBox.width > recAkt2.x + dataAk2.festeCollisionBox.width) {
					phyAk1.kollisionLeft = true;
					//System.out.println("leftcollison");
					double kraft = phyAk1.kraefte.get("leftKraft").x/2;
					phyAk1.kraefte.get("leftKraft").x = 0;
					event.notify("add_collision_force", akteur1.GetId(), (double) kraft + 20000, (double) 0  );
					event.notify("add_collision_force", akteur2.GetId(), (double) - kraft - 20000, (double) 0  );
				}
				break;
			case "right":
				//rightnKontrolle
				if(recAkt1.x + dataAk1.festeCollisionBox.width /*+7*/> recAkt2.x && recAkt1.x + dataAk1.festeCollisionBox.width < recAkt2.x + dataAk2.festeCollisionBox.width) {
					phyAk1.kollisionRight = true;
					//System.out.println("rightcollison");
					double kraft = phyAk1.kraefte.get("rightKraft").x/2;
					phyAk1.kraefte.get("rightKraft").x = 0;
					event.notify("add_collision_force", akteur1.GetId(), (double) - kraft - 10000 ,(double) 0 );
					event.notify("add_collision_force", akteur2.GetId(), (double) kraft + 10000 ,(double) 0 );
				}
				break;
				
			}
		}
	}
			
			
	/**
	 * Ueberpruefe eine Spielakteure auf eine Kollsion mit einem festen Tile, sowie auf Checkpoints und Zielline
	 * @param akteur
	 */
	public void checkTileCollisionAndCheckpoints(Spielakteur akteur) {//CheckCheckpoints fehlt noch
		//Sich die DatenSKomponente holen
		AKomponenteAkteurDaten data = (AKomponenteAkteurDaten) akteur.GetKomponente(1);
		APhysikKomponente phy = (APhysikKomponente) akteur.GetKomponente(2);
		
		//Benoetigte Werte berechnene
		int akteurLinksWeltX = (int) akteur.position.x + data.festeCollisionBox.x;
		int akteurRechtsWeltX = (int) akteur.position.x + data.festeCollisionBox.x + data.festeCollisionBox.width;
		int akteurTopWeltY = (int) akteur.position.y + data.festeCollisionBox.y;
		int akteurBottomWeltY = (int) akteur.position.y + data.festeCollisionBox.y + data.festeCollisionBox.height;
		
		int akteurLinkeSpalte = akteurLinksWeltX/optionen.tileGroesse;
		int akteurRechteSpalte = akteurRechtsWeltX/optionen.tileGroesse;
		int akteurTopZeile = akteurTopWeltY/optionen.tileGroesse;
		int akteurBottomZeile = akteurBottomWeltY/ optionen.tileGroesse;
		
		Tile tileNummer1, tileNummer2;

		//Physikkommponente auf Default wert setzten
		phy.collision = false;
		
			//KollisionCheck up
			akteurTopZeile = (akteurTopWeltY - 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
			tileNummer1 = spiellogik.kartenmanager.mapTiles[akteurLinkeSpalte][akteurTopZeile];
			tileNummer2 = spiellogik.kartenmanager.mapTiles[akteurRechteSpalte][akteurTopZeile];
			if(tileNummer1.collision == true || tileNummer2.collision == true) {
				double kraft = phy.kraefte.get("upKraft").y;
				phy.kraefte.get("upKraft").y = 0;
				phy.kraefte.get("downKraft").y += (kraft * -1)/optionen.abprallen + 10000;
				phy.collision = true;
			}
			//CheckpointCheck up
			if(tileNummer1.Checkpoint != 0 || tileNummer2.Checkpoint != 0) {
				if(tileNummer1.Checkpoint == data.checkpoints + 1 || tileNummer2.Checkpoint == data.checkpoints + 1) {
					data.checkpoints ++; //Vielleicht in Checkpoint event
					event.notify("checkpoint_event", akteur.GetId());
					System.out.println("Pass checkpoint: " + data.checkpoints);
				}
				else {
					event.notify("false_checkpoint_event", akteur.GetId());
				}
			}
			//ZielCheck up
			if(tileNummer1.Ziel == true || tileNummer2.Ziel == true) {
				
				Date jetzt = new Date();
				
				if(data.checkpoints == optionen.maxCheckpoint) {
					data.anzahlRunden++;//Erhoehe Rundenzahl //Event besser?
					data.checkpoints = 0;
					data.addRundenZeit(jetzt);
					data.rundenstart = jetzt.getTime();
					event.notify("ziel_event", akteur.GetId());
				}
				else {
					data.rundenstart = jetzt.getTime();
				}
			}
			
			
			//Kollision Check down
			akteurBottomZeile = (akteurBottomWeltY + 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
			tileNummer1 = spiellogik.kartenmanager.mapTiles[akteurLinkeSpalte][akteurBottomZeile];
			tileNummer2 = spiellogik.kartenmanager.mapTiles[akteurRechteSpalte][akteurBottomZeile];
			if(tileNummer1.collision == true || tileNummer2.collision == true) {
				double kraft = phy.kraefte.get("downKraft").y;
				phy.kraefte.get("downKraft").y = 0;
				phy.kraefte.get("upKraft").y -= kraft/optionen.abprallen + 10000;
				phy.collision = true;
			}
			//CheckpointCheck down
			if(tileNummer1.Checkpoint != 0 || tileNummer2.Checkpoint != 0) {
				if(tileNummer1.Checkpoint == data.checkpoints + 1 || tileNummer2.Checkpoint == data.checkpoints + 1) {
					data.checkpoints ++; //Vielleicht in Checkpoint event
					event.notify("checkpoint_event", akteur.GetId());
					System.out.println("Pass checkpoint: " + data.checkpoints);
				}
				else {
					event.notify("false_checkpoint_event", akteur.GetId());
				}
			}
			//ZielCheck down
			if(tileNummer1.Ziel == true || tileNummer2.Ziel == true) {
				Date jetzt = new Date();
				
				if(data.checkpoints == optionen.maxCheckpoint) {
					data.anzahlRunden++;//Erhoehe Rundenzahl //Event besser?
					data.checkpoints = 0;
					data.addRundenZeit(jetzt);
					data.rundenstart = jetzt.getTime();
					event.notify("ziel_event", akteur.GetId());
				}
				else {
					data.rundenstart = jetzt.getTime();
				}
			}
			

			//Kollision Detection left
			akteurLinkeSpalte = (akteurLinksWeltX - 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
			tileNummer1 = spiellogik.kartenmanager.mapTiles[akteurLinkeSpalte][akteurTopZeile];
			tileNummer2 = spiellogik.kartenmanager.mapTiles[akteurLinkeSpalte][akteurBottomZeile];
			if(tileNummer1.collision == true || tileNummer2.collision == true) {
				double kraft = phy.kraefte.get("leftKraft").x;
				phy.kraefte.get("leftKraft").x = 0;
				phy.kraefte.get("rightKraft").x += (-1*kraft)/optionen.abprallen + 10000;
				phy.collision = true;
			}
			//CheckpointCheck left
			if(tileNummer1.Checkpoint != 0 || tileNummer2.Checkpoint != 0) {
				if(tileNummer1.Checkpoint == data.checkpoints + 1 || tileNummer2.Checkpoint == data.checkpoints + 1) {
					data.checkpoints ++; //Vielleicht in Checkpoint event
					event.notify("checkpoint_event", akteur.GetId());
					System.out.println("Pass checkpoint: " + data.checkpoints);
				}
				else {
					event.notify("false_checkpoint_event", akteur.GetId());
				}
			}
			//ZielCheck left
			if(tileNummer1.Ziel == true || tileNummer2.Ziel == true) {
				Date jetzt = new Date();
				
				if(data.checkpoints == optionen.maxCheckpoint) {
					data.anzahlRunden++;//Erhoehe Rundenzahl //Event besser?
					data.checkpoints = 0;
					data.addRundenZeit(jetzt);
					data.rundenstart = jetzt.getTime();
					event.notify("ziel_event", akteur.GetId());
				}
				else {
					data.rundenstart = jetzt.getTime();
				}
			}
			

			//Kollision Detection right
			akteurRechteSpalte = (akteurRechtsWeltX + 7/*Pixel vorlaeufig*/)/optionen.tileGroesse; //Errechnung wo man max als naechstes sein wird
			tileNummer1 = spiellogik.kartenmanager.mapTiles[akteurRechteSpalte][akteurTopZeile];
			tileNummer2 = spiellogik.kartenmanager.mapTiles[akteurRechteSpalte][akteurBottomZeile];
			if(tileNummer1.collision == true || tileNummer2.collision == true) {
				double kraft = phy.kraefte.get("rightKraft").x;
				phy.kraefte.get("rightKraft").x = 0;
				phy.kraefte.get("leftKraft").x -= kraft/optionen.abprallen + 10000;
				phy.collision = true;
			}
			//CheckpointCheck right
			if(tileNummer1.Checkpoint != 0 || tileNummer2.Checkpoint != 0) {
				if(tileNummer1.Checkpoint == data.checkpoints + 1 || tileNummer2.Checkpoint == data.checkpoints + 1) {
					data.checkpoints ++; //Vielleicht in Checkpoint event
					event.notify("checkpoint_event", akteur.GetId());
					System.out.println("Pass checkpoint: " + data.checkpoints);
				}
				else {
					event.notify("false_checkpoint_event", akteur.GetId());
				}
			}
			//ZielCheck right
			if(tileNummer1.Ziel == true || tileNummer2.Ziel == true) {
				Date jetzt = new Date();
				
				if(data.checkpoints == optionen.maxCheckpoint) {
					data.anzahlRunden++;//Erhoehe Rundenzahl //Event besser?
					data.checkpoints = 0;
					data.addRundenZeit(jetzt);
					data.rundenstart = jetzt.getTime();
					event.notify("ziel_event", akteur.GetId());
				}
				else {
					data.rundenstart = jetzt.getTime();
				}
			
			}			
		}

}
