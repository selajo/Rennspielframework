package gui_opengl;

import anwendungsschicht.Spieloptionen;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * unterschiedliche Spielstadien
 */
enum GameState {
    GAME_ACTIVE,
    GAME_MENU,
    GAME_WIN
};

/**
 * Klasse die OpenGL Spielfenster Verwaltet
 */
public class Game {
    GameState State;
    public boolean Keys [];
    public int width, height;
    SpriteRenderer Renderer;
    GameLevel level;
    Spieloptionen optionen;
    GameObject player;
    SpielobjekteManager manager;

    /**
     * Konstruktor
     * @param width Spielfeldbreite
     * @param height Spielfeldhoehe
     */
    public Game(int width, int height){
        this.State = GameState.GAME_ACTIVE;
        this.Keys = new boolean[1024];
        this.width = width;
        this.height = height;
        optionen = Spieloptionen.getInstance();
    }

    /**
     * Spielfeldinitialisierungsfunktion
     */
    void Init(){
        ResourceManager resManager = ResourceManager.getIntstance();
        ResourceManager.loadShader("Res/shaders/vertexfragmentShader.glsl", "sprite");

        Matrix4f projection = new Matrix4f();
        projection.ortho(0.0f, this.width,0.0f, this.height, 0.0f, 100.0f);

        Shader aktshader = ResourceManager.getShader("sprite");
        aktshader.use();
        aktshader.uploadInt("image", 0);
        aktshader.uploadMat4f("projection", projection);

        Renderer = new SpriteRenderer(ResourceManager.getShader("sprite"));

        //load textures
        ResourceManager.loadAllMapTiles();
        ResourceManager.loadAllCarTiles();
        ResourceManager.loadTexture("Res/Player/AutoSprites/BlauAutodown.png", true, "BlauAutodown");



        //load levels
        level = new GameLevel();
        //level.Load("Res/Map/testmap.txt", optionen.maxBildschirmSpalten, optionen.maxBildschirmZeilen);
        level.init(optionen.mapTileNum);

        //initialize Player
        player = new GameObject(new Vector2f(this.width/2.0f - 48/2, this.height/2.0f - 48/2), new Vector2f(48, 48), ResourceManager.getTexture("BlauAutodown"), new Vector3f(1.0f, 1.0f, 1.0f), new Vector2f(0.0f, 0.0f));
        manager = new SpielobjekteManager();
    }

    /**
     * Funktion die Spielereingaben verwaltet
     * @param dt
     */
    void ProcessInput(float dt){
       /* if(Keys[GLFW_KEY_A]){
            if(player.Position.x >= 0.0f)
                player.Position.x -= 10;
        }
        if(Keys[GLFW_KEY_D]){
            if(player.Position.x <= (width - player.Size.x));
                player.Position.x += 10;
        }
        if(Keys[GLFW_KEY_W]){
            if(player.Position.y >= 0.0f)
                player.Position.y -= 10;
        }
        if(Keys[GLFW_KEY_S]){
            if(player.Position.x <= (height - player.Size.y));
                player.Position.x += 10;
        }*/

    }
    void Update(float dt){

    }

    /**
     * Die Zeichenfunktion, dort wird die Reihenfolge festgelegt in der die Spielobjektet gezeichnet werden
     */
    void Render (){
        //Draw Background
        Renderer.DrawSprite(ResourceManager.getTexture("20"), new Vector2f(0.0f, 0.0f), new Vector2f(this.width, this.height), new Vector3f(0.0f, 1.0f, 0.0f));
        //draw level
        level.Draw(Renderer);
        //draw player
        //player.Draw(Renderer);

        //draw gameObjects
        manager.Draw(Renderer);



        //Renderer.DrawSprite(ResourceManager.getTexture("32"), new Vector2f(20.0f, 20.0f), new Vector2f(this.width, this.height), new Vector3f(0.0f, 1.0f, 0.0f));
    }
}
