package com.fyp.communication.app;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

public class DatabaseConnector {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public DatabaseConnector(String userName, String userPwd){
        String uri = String.format("mongodb+srv://%s:%s@cluster0.a5jdgea.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0", userName, userPwd);
        
        try {
            mongoClient = MongoClients.create(uri);
            System.out.println("Connected to the database");
        } catch (MongoException me){
            System.err.println("Unable to connect to the database due to: " + me);
        }
    }

    public MongoDatabase getDatabase(String dbName){
        try {
            database = mongoClient.getDatabase(dbName);
            return database;
        } catch (MongoException me){
            System.err.println("Unable to connect to the database due to: " + me);
            return null;
        }
    }

    public MongoCollection<Document> getCollection(String collectionName){
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            return collection;
        } catch (MongoException me){
            System.err.println(String.format("Unable to get the collection %s due to: %s", collectionName, me));
            return null;
        }
    }

    public InsertOneResult insertDocument(MongoCollection<Document> collection, String datetime, float value){
        try {
            InsertOneResult result = collection.insertOne(new Document()
            .append("_id", new ObjectId())
            .append("datetime", datetime)
            .append("value", value));

            return result;
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
            return null;
        }
    }
}
