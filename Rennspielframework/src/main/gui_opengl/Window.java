package gui_opengl;


import anwendungsschicht.Spieloptionen;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private boolean created = false;
    private int width, height;
    private String title;
    private long glfwWindow;
    private Game testGame;
    public MenschlicheAnsichtOpenGL ansicht;
    public Spieloptionen optionen;
    public KeyListener listener;

    private static Window window = null;

    public void Init(MenschlicheAnsichtOpenGL ansicht){
        this.ansicht = ansicht;
    }

    private Window() {
        optionen = Spieloptionen.getInstance();

        this.width = optionen.bildschirmBreite;
        this.height = optionen.bildschirmHoehe;
        this.title = "2D Race Game";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        //OPenGL Einstellungen
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        //WindowHint
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        //Set the KeyListner in the Window
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        //Resize the OpenGl Context in the window
        glfwSetFramebufferSizeCallback(glfwWindow, Window::framebuffer_size_callback);
        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //Init KeyListener
        listener = KeyListener.get();
        listener.init(ansicht);

        //init Game
        testGame = new Game(width, height);
        testGame.Init();

        created = true;
    }

    public void loop() {

        float deltaTime = 0.0f;
        float lastFrame = 0.0f;

        //RenderLoop
        while (!glfwWindowShouldClose(glfwWindow)) {

            //KeyInput
            processInput(glfwWindow);

            float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            //manage user Input
            testGame.ProcessInput(deltaTime);

            //update Game sate
            testGame.Update(deltaTime);

            //Render Comands herer
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            testGame.Render();

            //TestKeyListener
            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE))
                System.out.println("Space is pressed");

            listener.checkInput();



            //check and call events and swap the buffers
            // Poll events
            glfwPollEvents();
            //DoubleBuffering
            glfwSwapBuffers(glfwWindow);
        }

        glfwTerminate();
    }

    public void update(float deltaTime){

        if(created == false){
            init();
        }

        //float deltaTime = 0.0f;
        //float lastFrame = 0.0f;

        //RenderLoop
        if (!glfwWindowShouldClose(glfwWindow)) {

            //KeyInput
            processInput(glfwWindow);

            /*float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;*/

            //manage user Input
            testGame.ProcessInput(deltaTime);

            //update Game sate
            testGame.Update(deltaTime);

            //Render Comands herer
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            testGame.Render();

            //TestKeyListener
            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE))
                System.out.println("Space is pressed");

            listener.checkInput();



            //check and call events and swap the buffers
            // Poll events
            glfwPollEvents();
            //DoubleBuffering
            glfwSwapBuffers(glfwWindow);
        }


    }


    public void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);

    }

    public static void framebuffer_size_callback(long window, int width, int height) {
        glViewport(0, 0, width, height);
    }
}