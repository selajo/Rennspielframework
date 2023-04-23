package gui_opengl;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Klass, die ein Spielobjekt wie ein Fahrzeug oder Spieltile repaesentiert
 */
public class
GameObject {
    //object state
    public Vector2f Position, Size, Velocity;
    public Vector3f Color;
    public String direction; //Richtung des Autos
    //public boolean IsSolid;
    //public boolean Destroyed;
    //render state
    public Texture Sprite;
    //constructor
    public GameObject(){
        Position = new Vector2f(0.0f, 0.0f);
        Size = new Vector2f(0.0f, 0.0f);
        Velocity = new Vector2f(0.0f, 0.0f);
        Color = new Vector3f(1.0f, 1.0f, 1.0f);
        Sprite = new Texture();
        //??
        direction = "up";

        //IsSolid = false;
        //Destroyed = false;
    }

    /**
     * Spezieller Konstruktor
     * @param pos
     * @param size
     * @param sprite
     * @param color
     * @param Velocity
     */
    public GameObject(Vector2f pos, Vector2f size, Texture sprite, Vector3f color, Vector2f Velocity){
        this.Position = pos;
        this.Size = size;
        this.Sprite = sprite;
        this.Color = color;
        this.Velocity = Velocity;
    }

    /**
     * Zeichenfunktion
     * @param renderer
     */
    void Draw(SpriteRenderer renderer){
        renderer.DrawSprite(this.Sprite, this.Position, this.Size, this.Color);
    }


}
