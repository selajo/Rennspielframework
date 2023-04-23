package gui_opengl;

import anwendungsschicht.EventListener;
import anwendungsschicht.EventManager;
import spiellogikschicht.Spielstadien;

import java.awt.event.KeyEvent;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Klasse für einen selbst definierten KeyListener, für das erkennen von Tastaturinput
 */
public class KeyListener {
    private MenschlicheAnsichtOpenGL ansicht;
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[350];
    EventManager event;
    private boolean up, down, left, right;

    private KeyListener() {
        event = EventManager.getInstance();
    }

    /**
     * Initialisierungsfunktion
     * @param ansicht Ansicht
     */
    public void init(MenschlicheAnsichtOpenGL ansicht){
        this.ansicht = ansicht;
    }

    /**
     * Singelton Funktion
     * @return
     */
    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }

        return KeyListener.instance;
    }

    /**
     * Funktion für Callback, definiert nach vorlage von LWGL
     * @param window
     * @param key
     * @param scancode
     * @param action
     * @param mods
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
            //https://www.glfw.org/docs/3.3/input_guide.html#input_keyboard

        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
        }
    }

    /**
     * Erkennt KeyInput
     * @param keyCode
     * @return
     */
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }

    public void checkInput() {
        boolean upPressed = false, downPressed = false, leftPressed = false, rightPressed = false;


        if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
            upPressed = true;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
            downPressed = true;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
            leftPressed = true;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
            rightPressed = true;
        }

        //if(this.ansicht.status == Spielstadien.Status_Spiel_Laeuft) {
            event.notify("key_event", this.ansicht.SpielerID, upPressed, downPressed, leftPressed, rightPressed);
        //}

    }
}