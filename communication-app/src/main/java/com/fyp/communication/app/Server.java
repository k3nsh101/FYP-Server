package com.fyp.communication.app;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    private Socket clientSocket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream din	 = null;
	private DataOutputStream dout = null;
	private BufferedReader br = null;

    public Server(int port, DatabaseConnector dbConnector){
        try
		{
            MongoDatabase database = dbConnector.getDatabase("FYP");
            MongoCollection<Document> frequencyCollection = database.getCollection("frequency");
            MongoCollection<Document> socOneCollection = database.getCollection("soc_one");

			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);

			System.out.println("Waiting for a client ...");

			clientSocket = serverSocket.accept();
			System.out.println("Client accepted " + clientSocket.getInetAddress());

			// takes input from the client socket
			din = new DataInputStream(clientSocket.getInputStream());
			
			dout = new DataOutputStream(clientSocket.getOutputStream());

			br = new BufferedReader(new InputStreamReader(System.in));

			Float lineFromClient = 0.0f;
			Float lineToClient = 0.0f;

            int inputNumber = 0;

			Boolean sendFlag = false;
			Boolean receiveFlag = true;

            while (true){
                if (receiveFlag){
                    try {
                        lineFromClient = din.readFloat();
                        System.out.println(lineFromClient);

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Colombo"));
                        Instant timestamp = Instant.now();
                        ZonedDateTime colomboTime = timestamp.atZone(ZoneId.of("Asia/Colombo"));                        

                        // send data to database
                        try {
                            if (inputNumber == 0) {
                                InsertOneResult result = dbConnector.insertDocument(frequencyCollection, dtf.format(colomboTime), lineFromClient);
                                System.out.println("Success! Inserted document to collection frequency with id: " + result.getInsertedId());
                
						} else if (inputNumber == 1) {
							InsertOneResult result = dbConnector.insertDocument(socOneCollection, dtf.format(colomboTime), lineFromClient);
                            System.out.println("Success! Inserted document to collection soc_one with id: " + result.getInsertedId());
							inputNumber = -1;
						}

						inputNumber++;

                        } catch (MongoException me){
                            System.err.println("Error when inserting documents due to " + me);
                        }

                    } catch (IOException i){
                        System.out.println(i);
					    break;
                    }

                }

                // if (sendFlag){
                //     //get data from the database

                //     // send data
                // }
            }

			System.out.println("Closing connection");

			// close connection
			clientSocket.close();
			din.close();

        } catch(IOException i) {
            System.out.println(i);
        }
    }
}
