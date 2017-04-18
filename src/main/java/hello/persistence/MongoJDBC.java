package hello.persistence;

import com.mongodb.*;

public class MongoJDBC {

    public MongoClient mongoClient;
    public DB db;
    public DBCollection coll;

    public MongoJDBC() {

        //Connect to DB
        mongoClient = new MongoClient("localhost", 27017);
        db = mongoClient.getDB("test");

        //Check if collection exists
        //If not, create
        if (!db.collectionExists("mycol")) {
            coll = db.createCollection("mycol", null);
        }

        //Get collection
        coll = db.getCollection("mycol");

        /*Insert object
        BasicDBObject doc = new BasicDBObject("title", "MongoDB").
                append("description", "database").
                append("likes", 100).
                append("url", "http://www.tutorialspoint.com/mongodb/").
                append("by", "tutorials point");
        coll.insert(doc);*/

        /*Find documents
        DBCursor cursor = coll.find();
        int i = 1;

        while (cursor.hasNext()) {
            System.out.println("Inserted Document: "+i);
            System.out.println(cursor.next());
            i++;
        }*/


    }

    public static void insert(DBCollection coll, BasicDBObject doc){
        coll.insert(doc);
    }



}

