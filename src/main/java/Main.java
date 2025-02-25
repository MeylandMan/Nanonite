import Core.App;
import Core.BlockModel;
import Core.Logger;
import Core.ModelLoader;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Logger.log(Logger.Level.INFO, "Creating the Window");

        ModelLoader modelLoader = new ModelLoader();
        try {

            modelLoader.loadModel("blocks/dirt");
            modelLoader.loadModel("blocks/grass_block");
        } catch (IOException e) {
            Logger.log(Logger.Level.ERROR, "Failed to load model: " + e.getMessage());
        }

        App app = new App(1280, 720, "MyCraft");
        app.run();
    }

}