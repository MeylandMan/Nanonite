package net;

import net.Core.*;

public class Main {

    public static void main(String[] args) {
        Logger.log(Logger.Level.INFO, "Creating the Window");
        Client.loadModels();
        App app = new App(1280, 720, Client.name);
        app.run();
    }

}