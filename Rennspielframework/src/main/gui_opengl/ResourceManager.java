package gui_opengl;

import anwendungsschicht.Spieloptionen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    public static ResourceManager instance = null;

    private static Map<String, Shader> shaders= new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<Integer, Map <Integer, Texture>> carTextures = new HashMap<>();

    public static Shader loadShader(String resourceName, String name){
        Shader neuShader = loadShaderFromFile(resourceName);
        shaders.put(name, neuShader);
        return neuShader;
    }

    public static Shader getShader(String name){
        return shaders.get(name);
    }

    public static Texture loadTexture(String resourceName, boolean alpha, String name){
        Texture neuTexture = loadTextureFromFile(resourceName, alpha);
        textures.put(name, neuTexture);
        return neuTexture;
    }

    public static Texture loadTextureBufferedImage(BufferedImage image, String name){
        Texture neuTexture = new Texture();
        neuTexture.setBufferedImage(image);
        textures.put(name, neuTexture);
        return  neuTexture;
    }

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


    public static Texture getTexture(String name){
        return textures.get(name);
    }

    public static Texture getCarTexture(String autotyp, String direction){
        Texture tex = carTextures.get(Integer.parseInt(autotyp)).get(Integer.parseInt(direction));
        return tex; //carTextures.get(autotyp).get(direction);
    }

    private ResourceManager(){};

    private static Shader loadShaderFromFile(String rescourceName){
        Shader shader = new Shader(rescourceName);
        shader.compile();
        return shader;
    }

    private static Texture loadTextureFromFile(String resourceName, boolean alpha){
        Texture texture;
        if(alpha){
          //Vielleicht überprüfen auf Alphakannal
        }
        texture = new Texture();
        texture.init(resourceName);

        return texture;
    }

    public static ResourceManager getIntstance(){
        if(instance == null) {
        instance = new ResourceManager();
        }
        return instance;
    }

    public static void loadAllMapTiles() {
        Spieloptionen optionen = Spieloptionen.getInstance();

        Map<Integer, BufferedImage> tileMap = optionen.spielfeldTiles;
        for (Map.Entry<Integer, BufferedImage> entry : tileMap.entrySet()) {
            ResourceManager.loadTextureBufferedImage(entry.getValue(), entry.getKey().toString());
        }
    }

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
