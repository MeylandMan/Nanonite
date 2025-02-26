package net;

import net.Core.*;

public class Main {

    public static void main(String[] args) {
        Logger.log(Logger.Level.INFO, "Creating the Window");
        Client.loadModels();
        BlockModel blockModel = Client.modelLoader.getModel("blocks/dirt");
        App app = new App(1280, 720, Client.name);
        app.run();
    }

}