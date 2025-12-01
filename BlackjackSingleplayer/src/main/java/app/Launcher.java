package app;

/**
 * Launcher class to avoid JavaFX module issues with fat JAR
 * This class doesn't extend Application, so it can be used as main class in shaded JAR
 * It simply delegates to MainApp which does extend Application
 * 
 * @author Group 12
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
