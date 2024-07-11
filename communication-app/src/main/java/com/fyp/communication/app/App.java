package com.fyp.communication.app;

import java.util.Properties;

public class App 
{
    public static void main( String[] args )
    {
        ConfigReader configReader = new ConfigReader();
        Properties appProps = configReader.getConfig();

        int port = Integer.parseInt(appProps.getProperty("PORT"));
        String userName = appProps.getProperty("DB_USER");
        String userPwd = appProps.getProperty("DB_PASSWORD");

        DatabaseConnector dbConnector = new DatabaseConnector(userName, userPwd);
        
        Server server = new Server(port, dbConnector);        
    }
}
