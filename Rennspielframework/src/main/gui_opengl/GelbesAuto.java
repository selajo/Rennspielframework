package gui_opengl;

import anwendungsschicht.Spieloptionen;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;

public class GelbesAuto extends GameObject{

    Texture up1, down1, left1, right1;

    GelbesAuto(){
        Spieloptionen optionen = Spieloptionen.getInstance();

        up1 = ResourceManager.getCarTexture("4", "1");
        down1 = ResourceManager.getCarTexture("4", "2");
        left1 = ResourceManager.getCarTexture("4", "3");
        right1 = ResourceManager.getCarTexture("4", "4");

        //Tile Default Werte
        Position = new Vector2f(100, 100);
        Size = new Vector2f(optionen.tileGroesse, optionen.tileGroesse);
        Color = new Vector3f(0.5f, 0.5f, 0.5f);
        Velocity = new Vector2f(0.0f, 0.0f);
    }

    void Draw(SpriteRenderer renderer){
        Texture image = null;

        switch(direction) {
            case "up":
                image = up1;
                break;
            case "down":
                image = down1;
                break;
            case "left":
                image = left1;
                break;
            case "right":
                image = right1;
                break;

        }

        renderer.DrawSprite(image, this.Position, this.Size, this.Color);
    }
}
