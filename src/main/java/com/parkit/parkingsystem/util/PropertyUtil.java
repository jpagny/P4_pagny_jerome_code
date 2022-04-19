package com.parkit.parkingsystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    public static Properties loadConfProperty() {

        try (InputStream input = PropertyUtil.class.getClassLoader().getResourceAsStream("configuration.properties")) {

            Properties property = new Properties();
            property.load(input);

            return property;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
