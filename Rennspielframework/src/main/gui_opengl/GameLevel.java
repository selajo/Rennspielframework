package gui_opengl;

import anwendungsschicht.Spieloptionen;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.Vector;

/**
 * Klasse, die Spiellevel verwaltet
 */
public class GameLevel {
    Spieloptionen spieloptionen = Spieloptionen.getInstance();

    //level state
    //Array mit allen Tiles, die grafische Eigenschaften definieren
    public Vector<GameObject> gameObjects = new Vector<GameObject>();
    //constructor
    public GameLevel(){}
    //load Level from file

    /**
     * Funktion, in der Spiellevel geladen wird
     * @param filepath
     * @param maxBildschirmSpalten
     * @param maxBildschirmZeilen
     */
    public void Load(String filepath, int maxBildschirmSpalten, int maxBildschirmZeilen){
        int [][] mapTileNum = new int[maxBildschirmSpalten][maxBildschirmZeilen];
        try{
        //LadeKartenInformation
        InputStream is ;
        BufferedReader br;

        try {
            is = getClass().getResourceAsStream(filepath);
            br = new BufferedReader(new InputStreamReader(is));
        }//Datei ist nicht im Jar-Paket enthalten -> lese von Disk
        catch (Exception e) {
            is = new FileInputStream(filepath);
            br = new BufferedReader(new InputStreamReader(is));
        }

        String line = " ";


        int col = 0;
        int row = 0;

        while (col < maxBildschirmSpalten && row < maxBildschirmZeilen) {

            line = br.readLine();
            while (col < maxBildschirmSpalten) {
                String numbers[] = line.split(" ");
                int num = Integer.parseInt(numbers[col]);
                mapTileNum[col][row] = num;
                col++;
            }
            if (col == maxBildschirmSpalten) {
                col = 0;
                row++;
            }
        }
        br.close();

    }catch ( IOException e) {
        e.printStackTrace();
    }

    init(mapTileNum);

    }

    /**
     * Zeichenfunktion
     * @param renderer
     */
    public void Draw(SpriteRenderer renderer){
        for(GameObject tile : this.gameObjects){
            tile.Draw(renderer);
        }

    }

    /**
     * Initialisierungsfunktion der Spielleveldaten
     * @param tileData
     */
    public void init (int [][] tileData){
        int height = spieloptionen.maxBildschirmZeilen;
        int width = spieloptionen.maxBildschirmSpalten;
        float unit_width = spieloptionen.tileGroesse;
        float unit_height = spieloptionen.tileGroesse ;

        int [][] inverseTileData = new int [width][height];
        //Tiles umdrehen //OpenGL ist spiegelverkehrt
        int j = 0;
        for(int i = spieloptionen.maxBildschirmZeilen-1; i>=0;  i--, j++){
            for(int k = 0; k < spieloptionen.maxBildschirmSpalten; k++){
                inverseTileData[k][j] = tileData[k][i];
            }
        }


        //initialize level tiles based on tileData

        for( int y = 0; y < height; ++y){
            for(int x = 0; x < width; ++x){
                Vector2f pos = new Vector2f( unit_width * x, unit_height * y);
                Vector2f size = new Vector2f(unit_width, unit_height);
                Integer tileNumber = inverseTileData[x][y];
                GameObject obj = new GameObject(pos, size, ResourceManager.getTexture(tileNumber.toString()), new Vector3f(1.0f, 1.0f, 1.0f), new Vector2f(0.0f, 0.0f));
                this.gameObjects.addElement(obj);

            }
        }

    }

}
