import Core.App;
import Core.Logger;

public class Main {

    public static void main(String[] args) {
        Logger.log(Logger.Level.INFO, "Creating the Window");
        App app = new App(1280, 720, "MyCraft");
        app.run();
    }

}