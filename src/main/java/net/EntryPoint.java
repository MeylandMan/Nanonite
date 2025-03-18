package net;

import net.Core.App;
import net.Core.Client;
import net.Core.Logger;
import net.Core.MultiThreading;

public class EntryPoint {

    public static void main(String[] args) {


        Logger.log(Logger.Level.INFO, "Nombre de threads max : " + Client.MAX_THREADS);
        Client.loadModels();
        Logger.log(Logger.Level.INFO, "Creating Window");

        App app = new App(640, 480, Client.name);
        app.run();

    }
}