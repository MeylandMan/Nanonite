package net.Core;

import net.Core.Rendering.Texture;

import java.io.IOException;

public class Client {
    public static int renderDistance = 8;
    public static final int MAX_RENDER_DISTANCE = 16;
    public static final int MIN_RENDER_DISTANCE = 1;

    public static int Vsync = 1;

    public static String name = "Mycraft ";
    public static String type = "Vanilla";
    public static String version = "Alpha1.0";


    public static ModelLoader modelLoader = new ModelLoader();
    public static String[] blockTexturePath = {
            "blocks/dirt.png",
            "blocks/grass_block_side.png",
            "blocks/grass_block_top.png"
    };
    public static Texture[] blockTextures;

    public static void loadModels() {
        try {

            modelLoader.loadModel("blocks/dirt");
            modelLoader.loadModel("blocks/grass_block");
        } catch (IOException e) {
            Logger.log(Logger.Level.ERROR, "Failed to load model: " + e.getMessage());
        }
    }

    public static void LoadingBlockTextures() {
        blockTextures = new Texture[blockTexturePath.length];
        for(int i = 0; i < blockTextures.length; i++) {
            blockTextures[i] = new Texture(blockTexturePath[i]);
            Logger.log(Logger.Level.INFO, "Loading texture: " + blockTexturePath[i]);
        }
    }

    public static void DeleteTextures() {
        for(Texture texture : blockTextures)
            texture.Delete();
    }
}
