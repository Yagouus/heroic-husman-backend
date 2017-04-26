package hello.persistence;


import com.mongodb.*;



import java.util.*;

public class MongoJDBC {

    public MongoClient mongoClient;
    public static DB db;
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

    public static Set<String> getDBS(){
        return db.getCollectionNames();
    }

    public static HashMap<String, ArrayList<String>> getContent(String collName){

        DBCollection coll = db.getCollection(collName);

        //Get unique values
        HashMap<String, ArrayList<String>> data = new HashMap<>();

        List<DBObject> indexes = coll.getIndexInfo();

        //Create index for each field
        for (int i = 1; i < indexes.size(); i++) {
            String key = indexes.get(i).get("key").toString();
            String[] tokens = key.split("\"");

            data.put(tokens[1], (ArrayList<String>) coll.distinct(tokens[1]));
        }

        return data;

    }



}

