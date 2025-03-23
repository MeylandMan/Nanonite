package net.Core;

import net.Core.Rendering.Texture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class Client {
    public static int renderDistance = 16;
    public static final int MAX_RENDER_DISTANCE = 92;
    public static final int MIN_RENDER_DISTANCE = 8;

    public static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    public static int Vsync = 1;
    public static String name = "Mycraft ";
    public static String type = "Vanilla";
    public static String version = "Alpha1.0";


    public static String processorBrand = "";
    public static String GPUBrand = "";
    public static String glVersion;
    public static int mainGPU = 0;

    public static final String[] modelPaths = {
            "blocks/dirt",
            "blocks/grass_block",
            "blocks/stone",
            "blocks/gravel",
            "blocks/bedrock",
            "blocks/deepslate",
            "blocks/water",
            "blocks/sand"
    };

    public static ModelLoader modelLoader = new ModelLoader();
    public static ArrayList<String> blockTexturePath = new ArrayList<>();
    public static Texture[] blockTextures;

    public static int[] samplers;

    public static void loadModels() {
        try {
            for(String modelPath : modelPaths) {
                modelLoader.loadModel(modelPath, ModelType.BLOCKS);
            }
        } catch (IOException e) {
            Logger.log(Logger.Level.ERROR, "Failed to load model: " + e.getMessage());
        }

        loadingTexturesPath();
    }

    public static void addTexturePath(String path) {
        blockTexturePath.add(path);
        Logger.log(Logger.Level.INFO, "Added texture path: " + path);
    }

    private static void loadingTexturesPath() {
        for(int i = 0; i < modelPaths.length; i++) {
            BlockModel model = modelLoader.getModel(modelPaths[i]);

            if(blockTexturePath.isEmpty())
                addTexturePath(model.getTexture("north"));

            if(CheckTexturePath(model, "north")) {
                addTexturePath(model.getTexture("north"));
            }
            if(CheckTexturePath(model, "south")) {
                addTexturePath(model.getTexture("south"));
            }
            if(CheckTexturePath(model, "down")) {
                addTexturePath(model.getTexture("down"));
            }
            if(CheckTexturePath(model, "up")) {
                addTexturePath(model.getTexture("up"));
            }
            if(CheckTexturePath(model, "east")) {
                addTexturePath(model.getTexture("east"));
            }
            if(CheckTexturePath(model, "west")) {
                addTexturePath(model.getTexture("west"));
            }
        }

        samplers = new int[blockTexturePath.size()];
        for(int i = 0; i < blockTexturePath.size(); i++) {
            samplers[i] = i;
        }
    }

    private static boolean CheckTexturePath(BlockModel model, String key) {
        String texture = model.getTexture(key);
        for (String s : blockTexturePath) {
            if (texture.equals(s)) {
                return false;
            }
        }
        return true; // The string is not found
    }
    public static void LoadingBlockTextures() {
        blockTextures = new Texture[blockTexturePath.size()];
        for(int i = 0; i < blockTextures.length; i++) {
            blockTextures[i] = new Texture(blockTexturePath.get(i));
        }
    }

    public static void DeleteTextures() {
        for(Texture texture : blockTextures)
            texture.Delete();
    }
}
