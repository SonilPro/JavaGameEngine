package launcher;

import core.EngineManager;
import core.WindowManager;

public class Launcher {
    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager("Test", 1080, 810, false);
        game = new TestGame();
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}
