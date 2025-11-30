package network;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * API configuration - loads API URL from secrets.properties
 * 
 * @author COP4331 Team
 */
public class ApiConfig {
    
    private static final Properties secretProperties = new Properties();
    private static final String DEFAULT_API_URL = "https://blackjack-api-835797459730.us-central1.run.app/api";
    
    static {
        // Load secrets.properties
        try {
            try (FileInputStream fis = new FileInputStream("secrets.properties")) {
                secretProperties.load(fis);
                System.out.println("[API] Loaded secrets.properties");
            } catch (IOException e) {
                try (InputStream is = ApiConfig.class.getClassLoader().getResourceAsStream("secrets.properties")) {
                    if (is != null) {
                        secretProperties.load(is);
                        System.out.println("[API] Loaded secrets.properties from classpath");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[API] Could not load secrets.properties, using default URL");
        }
    }
    
    /**
     * Get the API URL from secrets.properties
     */
    public static String getApiUrl() {
        String apiUrl = secretProperties.getProperty("api.url", DEFAULT_API_URL);
        System.out.println("[API] Using API: " + apiUrl);
        return apiUrl;
    }
    
    private ApiConfig() {
        // Utility class - no instantiation
    }
}
