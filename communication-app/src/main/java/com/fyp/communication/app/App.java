package com.fyp.communication.app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

public class App
{
    //initialize socket and input stream
	private Socket clientSocket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream din	 = null;
	private DataOutputStream dout = null;
	private BufferedReader br = null;


	// constructor with port
	public App(int port)
	{
		// starts server and waits for a connection
		try
		{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            Properties appProps = new Properties();
            appProps.load(inputStream);
    
            String userName = appProps.getProperty("DB_USER");
            String userPwd = appProps.getProperty("DB_PASSWORD");

			String uri = String.format("mongodb+srv://%s:%s@cluster0.a5jdgea.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0", userName, userPwd);

			MongoDatabase database = null;
			MongoCollection<Document> frequencyCollection = null;
			MongoCollection<Document> socOneCollection = null;
			MongoCollection<Document> socOTwoCollection = null;
			MongoCollection<Document> socThreeCollection = null;

			MongoClient mongoClient = null;

			try {
				mongoClient = MongoClients.create(uri);
				database = mongoClient.getDatabase("FYP");
				frequencyCollection = database.getCollection("frequency");
				socOneCollection = database.getCollection("soc_one");
				socOTwoCollection = database.getCollection("soc_two");
				socThreeCollection = database.getCollection("soc_three");
				System.out.println("Connected to the database");
			} catch (MongoException me){
				System.err.println("Unable to connect to the database due to: " + me);
			}

			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);

			System.out.println("Waiting for a client ...");

			clientSocket = serverSocket.accept();
			System.out.println("Client accepted " + clientSocket.getInetAddress());

			// takes input from the client socket
			din = new DataInputStream(clientSocket.getInputStream());
			
			dout = new DataOutputStream(clientSocket.getOutputStream());

			br = new BufferedReader(new InputStreamReader(System.in));

			String lineFromClient = "";
			String lineToClient = "";

			// reads message from client until "Over" is sent
			while (!lineFromClient.equals("Over"))
			{
				try
				{
					lineFromClient = Float.toString(din.readFloat());
					System.out.println(lineFromClient);

					//lineToClient = br.readLine();
					//dout.writeUTF(lineToClient);
					//dout.flush();

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Colombo"));
					Instant timestamp = Instant.now();
					ZonedDateTime colomboTime = timestamp.atZone(ZoneId.of("Asia/Colombo"));

					try {
						// Inserts a sample document describing a movie into the collection
						InsertOneResult result = frequencyCollection.insertOne(new Document()
								.append("_id", new ObjectId())
								.append("datetime", dtf.format(colomboTime))
								.append("value", lineFromClient));
						// Prints the ID of the inserted document
						System.out.println("Success! Inserted document id: " + result.getInsertedId());
					
					// Prints a message if any exceptions occur during the operation
					} catch (MongoException me) {
						System.err.println("Unable to insert due to an error: " + me);
					}
				}
				catch(IOException i)
				{
					System.out.println(i);
					break;
				}
			}
			System.out.println("Closing connection");

			// close connection
			clientSocket.close();
			din.close();
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
	}

	public static void main(String args[])
	{
		App server = new App(5000);
	}
}
