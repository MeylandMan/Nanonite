package net;

import net.Core.App;
import net.Core.Client;
import net.Core.Logger;

public class EntryPoint {

    public static void main(String[] args) {
        Client.loadModels();

        Logger.log(Logger.Level.INFO, "Creating Window");
        App app = new App(1280, 720, Client.name);
        app.run();
    }
}