package com.fyp.communication.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private InputStream inputStream;
    private Properties appProps;

    public ConfigReader() {
        inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
        appProps = new Properties();
        try {
            appProps.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getConfig(){
        return appProps;
    }
}
