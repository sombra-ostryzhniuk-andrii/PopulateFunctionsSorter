package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.Schema;
import com.ifc.populationorderdeterminant.dto.SourceSchemas;
import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PropertiesProvider {

    private static Properties properties;
    private static final String PROPERTY_ARRAY_DELIMITER = ",";
    private static final String SCHEMA = "schema.";
    private static final String SOURCE_SCHEMAS = "source.schemas.";
    private static final String EXCLUDE_FUNCTIONS_PROPERTY = "exclude.functions.";

    private static String configFilePath;

    private static Properties getProperties() {
        if (properties == null) {
            synchronized (PropertiesProvider.class) {
                if (properties == null) {
                    if (configFilePath == null) {
                        configFilePath = CommandOptionsProvider.valueOfRequired(CommandOptionsProvider.CONFIG_FILE_PARAMETER);
                    }
                    properties = loadProperties(configFilePath);
                }
            }
        }
        return properties;
    }

    private static Properties loadProperties(final String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {

            Properties properties = new Properties();
            properties.load(input);
            return properties;

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find the configuration file " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load configuration properties", e);
        }
    }

    public static String getProperty(String propertyName) {
        String property = getProperties().getProperty(propertyName);
        if (property == null) {
            log.warn("Unable to find property '" + propertyName + "' in the configuration file " + configFilePath);
        }
        return property;
    }

    public static String getRequiredProperty(String propertyName) {
        String property = getProperties().getProperty(propertyName);
        if (property == null) {
            throw new RuntimeException("Unable to find property '" + propertyName + "' in the configuration file " + configFilePath);
        } else if (property.isEmpty()) {
            throw new RuntimeException("Property '" + propertyName + "' is empty. Please, configure the property in the " +
                    "configuration file " + configFilePath);
        }
        return property;
    }

    public static List<String> getPropertyAsList(String propertyName) {
        String property = getProperty(propertyName);
        return StringUtils.isEmpty(property)
                ? Collections.emptyList()
                : Stream.of(property.split(PROPERTY_ARRAY_DELIMITER))
                    .map(StringUtil::validateString)
                    .collect(Collectors.toList());
    }

    public static Set<String> getPropertyAsSet(String propertyName) {
        return new HashSet<>(getPropertyAsList(propertyName));
    }

    public static List<String> getRequiredPropertyAsList(String propertyName) {
        String property = getRequiredProperty(propertyName);
        return Stream.of(property.split(PROPERTY_ARRAY_DELIMITER))
                .map(StringUtil::validateString)
                .collect(Collectors.toList());
    }

    public static Set<String> getRequiredPropertyAsSet(String propertyName) {
        return new HashSet<>(getRequiredPropertyAsList(propertyName));
    }

    public static Set<Function> getExcludedFunctionsSet(String schema) {
        return PropertiesProvider.getPropertyAsList(EXCLUDE_FUNCTIONS_PROPERTY + schema)
                .stream()
                .map(functionName -> new Function(functionName, schema))
                .collect(Collectors.toSet());
    }

    public static Set<Schema> getSchemas() {
        Set<Schema> schemas = getProperties().stringPropertyNames()
                .stream()
                .filter(property -> property.contains(SCHEMA))
                .map(property -> property.substring(property.lastIndexOf(".") + 1))
                .map(key -> new Schema(PropertiesProvider.getRequiredProperty(SCHEMA + key), key))
                .collect(Collectors.toSet());

        if (schemas.isEmpty()) {
            throw new RuntimeException("Schemas are not configured. Please configure property " + SCHEMA +
                    "<population order> in the configuration file " + configFilePath);
        }
        return schemas;
    }

    public static Set<SourceSchemas> getSourceSchemasSet() {
        return getProperties().stringPropertyNames()
                .stream()
                .filter(property -> property.contains(SOURCE_SCHEMAS))
                .map(property -> property.substring(property.lastIndexOf(".") + 1))
                .map(key -> new SourceSchemas(key, PropertiesProvider.getRequiredPropertyAsSet(SOURCE_SCHEMAS + key)))
                .collect(Collectors.toSet());
    }

}
