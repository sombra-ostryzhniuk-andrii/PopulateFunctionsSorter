package com.ifc.populatefunctionssorter.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesProvider {

    private static Properties properties;
    private static final String PROPERTY_ARRAY_DELIMITER = ",";

//    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private static final String PROPERTIES_FILE_NAME = "config-impl.properties";

    private static Properties getProperties() {
        if (properties == null) {
            synchronized (PropertiesProvider.class) {
                if (properties == null) {
                    properties = loadProperties(PROPERTIES_FILE_NAME);
                }
            }
        }
        return properties;
    }

    private static Properties loadProperties(final String fileName) {
        try (InputStream input = PropertiesProvider.class.getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                throw new RuntimeException("Unable to find a config file");
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load configuration properties", e);
        }
    }

    public static String getProperty(String propertyName) {
        return getProperties().getProperty(propertyName);
    }

    public static List<String> getPropertyAsList(String propertyName) {
        String property = getProperty(propertyName);
        return Arrays.asList(property.split(PROPERTY_ARRAY_DELIMITER));
    }

}
