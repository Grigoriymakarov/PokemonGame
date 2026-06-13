import java.io.File;

/**
 * Launcher for starting the Main application with dynamic classpath management.
 * <p>Ensures the application can execute correctly even if the {@code lib/} directory
 * is located alongside the executable JAR file.</p>
 */
public class Launcher {
    
    public static void main(final String[] args) throws Exception {
        // Dynamically add the lib/ directory to the classpath
        Launcher.addLibraryPath();
        
        // Start the Main application
        Main.main(args);
    }
    
    /**
     * Dynamically adds the {@code lib/} directory to {@code java.library.path}.
     * <p>This allows JavaFX to locate and load the necessary native libraries.</p>
     * 
     * @throws Exception if an error occurs while configuring the library path
     */
    private static void addLibraryPath() throws Exception {
        // Determine the current working directory
        final String currentDir = System.getProperty("user.dir");
        final String libPath = new File(currentDir, "lib").getAbsolutePath();
        
        // Append to java.library.path
        final String existingPath = System.getProperty("java.library.path", "");
        final String newPath = libPath + File.pathSeparator + existingPath;
        System.setProperty("java.library.path", newPath);
    }
}
