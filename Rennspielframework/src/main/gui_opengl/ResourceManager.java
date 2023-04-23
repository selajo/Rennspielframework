package gui_opengl;

import anwendungsschicht.Spieloptionen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasse die OpenGL Ressourcen laed und verwaltet
 */
public class ResourceManager {

    public static ResourceManager instance = null;

    private static Map<String, Shader> shaders= new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<Integer, Map <Integer, Texture>> carTextures = new HashMap<>();

    /**
     * Laden es definierten Shaders
     * @param resourceName
     * @param name
     * @return
     */
    public static Shader loadShader(String resourceName, String name){
        Shader neuShader = loadShaderFromFile(resourceName);
        shaders.put(name, neuShader);
        return neuShader;
    }

    /**
     * Erhalte den geladenen Shader
     * @param name
     * @return
     */
    public static Shader getShader(String name){
        return shaders.get(name);
    }

    /**
     * Lade eine Textur
     * @param resourceName
     * @param alpha
     * @param name
     * @return
     */
    public static Texture loadTexture(String resourceName, boolean alpha, String name){
        Texture neuTexture = loadTextureFromFile(resourceName, alpha);
        textures.put(name, neuTexture);
        return neuTexture;
    }

    /**
     * Lade eine Textur die bereits ein Buffered Image ist
     * @param image
     * @param name
     * @return
     */
    public static Texture loadTextureBufferedImage(BufferedImage image, String name){
        Texture neuTexture = new Texture();
        neuTexture.setBufferedImage(image);
        textures.put(name, neuTexture);
        return  neuTexture;
    }

    /**
     * Lade eine Textur, die ein Fahrzeug repräsentiert
     * @param image
     * @param autotyp
     * @param direction
     * @return
     */
    public static Texture loadCarTextureBufferedImage(BufferedImage image, Integer autotyp, Integer direction){
        Texture neuTexture =  new Texture();
        neuTexture.setBufferedImage(image);
        if(carTextures.get(autotyp) == null){
            Map<Integer, Texture> neuMap = new HashMap<>();
            neuMap.put(direction, neuTexture);
            carTextures.put(autotyp, neuMap);
        }else{
            Map<Integer, Texture> neuMap = carTextures.get(autotyp);
            neuMap.put(direction, neuTexture);
            carTextures.put(autotyp,neuMap);
        }
        return  neuTexture;
    }


    /**
     * Liefert eine Textur
     * @param name
     * @return
     */
    public static Texture getTexture(String name){
        return textures.get(name);
    }

    /**
     * Rueckgabe einer Fahrzeugtextur
     * @param autotyp
     * @param direction
     * @return
     */
    public static Texture getCarTexture(String autotyp, String direction){
        Texture tex = carTextures.get(Integer.parseInt(autotyp)).get(Integer.parseInt(direction));
        return tex; //carTextures.get(autotyp).get(direction);
    }

    private ResourceManager(){};

    /**
     * Lade Shader von einer Datei
     * @param rescourceName
     * @return
     */
    private static Shader loadShaderFromFile(String rescourceName){
        Shader shader = new Shader(rescourceName);
        shader.compile();
        return shader;
    }

    /**
     * Lade Textur von einer Datei
     * @param resourceName
     * @param alpha
     * @return
     */
    private static Texture loadTextureFromFile(String resourceName, boolean alpha){
        Texture texture;
        if(alpha){
          //Vielleicht überprüfen auf Alphakannal
        }
        texture = new Texture();
        texture.init(resourceName);

        return texture;
    }

    /**
     * Erhalten den Singelton ResourceManager
     * @return
     */
    public static ResourceManager getIntstance(){
        if(instance == null) {
        instance = new ResourceManager();
        }
        return instance;
    }

    /**
     * Lade alle Tiles von den Spieloptionen
     */
    public static void loadAllMapTiles() {
        Spieloptionen optionen = Spieloptionen.getInstance();

        Map<Integer, BufferedImage> tileMap = optionen.spielfeldTiles;
        for (Map.Entry<Integer, BufferedImage> entry : tileMap.entrySet()) {
            ResourceManager.loadTextureBufferedImage(entry.getValue(), entry.getKey().toString());
        }
    }

    /**
     * Lade allte Fahrzeugtiles von den Spieloptionen
     */
    public static void loadAllCarTiles(){
        Spieloptionen optionen = Spieloptionen.getInstance();

        Map<Integer, Map <Integer, BufferedImage>> autoTiles= optionen.autoTiles;

        for(Map.Entry<Integer, Map <Integer, BufferedImage>> entry : autoTiles.entrySet()){
            Map<Integer, BufferedImage> innereMap = entry.getValue();
            for(Map.Entry<Integer, BufferedImage> entry1 : innereMap.entrySet()){
                //Hier wird in den Speicher geschrieben
                ResourceManager.loadCarTextureBufferedImage(entry1.getValue() , entry.getKey(), entry1.getKey());
            }
        }

    }






   /* public static Shader getShader(String rescourceName){
        File file = new File(rescourceName);
        if(AssetPool.shaders.containsKey(file.getAbsolutePath())){
            return AssetPool.shaders.get(file.getAbsolutePath());
        }else{
            Shader shader = new Shader(rescourceName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(),shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);
        if(AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        }else{
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(),texture);
            return texture;
        }
    }*/
}
