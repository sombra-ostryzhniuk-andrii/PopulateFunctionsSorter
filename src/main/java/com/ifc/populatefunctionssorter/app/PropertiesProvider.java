package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class PropertiesProvider {

    private static Properties properties;
    private static String schema;
    private static final String PROPERTY_ARRAY_DELIMITER = ",";
    private static final String EXCLUDE_FUNCTIONS_PROPERTY = "exclude.functions.";

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
        String property = getProperties().getProperty(propertyName);
        if (property == null) {
            log.warn("Unable to find property '" + propertyName + "' in the configuration file " + PROPERTIES_FILE_NAME);
        }
        return property;
    }

    public static List<String> getPropertyAsList(String propertyName) {
        String property = getProperty(propertyName);
        return StringUtils.isEmpty(property)
                ? Collections.emptyList()
                : Arrays.asList(property.split(PROPERTY_ARRAY_DELIMITER));
    }

    public static List<String> getExcludedFunctions() {
        return PropertiesProvider.getPropertyAsList(getExcludeFunctionsProperty())
                .stream()
                .map(StringUtil::validateString)
                .collect(Collectors.toList());
    }

    public static String getSchema() {
        if (schema == null) {
            schema = App.getSchema();
        }
        return schema;
    }

    public static String getExcludeFunctionsProperty() {
        return EXCLUDE_FUNCTIONS_PROPERTY + getSchema();
    }
}
