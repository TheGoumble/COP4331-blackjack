package network;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * API configuration - loads API URL from secrets.properties
 * 
 * @author Group 12
 */
public class ApiConfig {
    
    private static final Properties secretProperties = new Properties();
    
    static {
        // Load secrets.properties
        try {
            try (FileInputStream fis = new FileInputStream("secrets.properties")) {
                secretProperties.load(fis);
            } catch (IOException e) {
                try (InputStream is = ApiConfig.class.getClassLoader().getResourceAsStream("secrets.properties")) {
                    if (is != null) {
                        secretProperties.load(is);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[API] Could not load secrets.properties");
        }
    }
    
    /**
     * Get the API URL from secrets.properties
     */
    public static String getApiUrl() {
        return secretProperties.getProperty("api.url");
    }
    
    private ApiConfig() {
        // Utility class - no instantiation
    }
}
