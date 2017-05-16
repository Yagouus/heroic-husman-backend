package hello.persistence;

import com.mongodb.*;
import hello.dataTypes.Branch;
import hello.dataTypes.Hierarchy;
import hello.parser.parserCSV;
import hello.storage.StorageService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MongoDAO {

    public static MongoClient mongoClient = new MongoClient("localhost", 27017);
    public static DB db = mongoClient.getDB("test");
    public DBCollection coll;

    //Basic operations
    public static DBCollection createCollection(String name) {

        DBCollection coll;

        //If coll exists, override
        coll = db.createCollection(name, null);

        return coll;
    }

    public static DBCursor queryCollection(DBCollection coll, BasicDBObject query){
        return coll.find(query);
    }

    public static DBCollection getCollection(String name){
        return db.getCollection(name);
    }

    public static Set<String> getCollections(){
        return db.getCollectionNames();
    }

    public static void insertLog(String filePath, StorageService storageService) {

        //Create new collection
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        DBCollection coll;

        //If coll exists, override
        if (db.collectionExists(fileName)) {
            coll = db.getCollection(fileName);
            coll.drop();
        }
        coll = db.createCollection(fileName, null);

        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> headers = parserCSV.getHeaders(filePath).getData();
        //ArrayList<String> headers = parserCSV.getHeaders(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            //Skip headers
            line = br.readLine();

            //If file has content
            //For each line we create a doc
            while ((line = br.readLine()) != null) {

                String[] columns = line.split(cvsSplitBy);

                //Create doc
                BasicDBObject doc = new BasicDBObject();

                //Add content to doc
                for (int i = 0, columnsLength = columns.length; i < columnsLength; i++) {
                    String data = columns[i];
                    System.out.println(data.replace("\"", ""));
                    doc.append(headers.get(i), data.replace("\"", ""));
                }
                MongoJDBC.insert(coll, doc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static HashMap<String, ArrayList<String>> getContent(String collName) {

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

    public static Hierarchy queryLog(String file, Hierarchy h, StorageService storageService) {

        System.out.println(file);
        System.out.println(h.getData());

        Integer bIndex = 0;
        ArrayList<String> headers = new ArrayList<>();

        //Data to return
        Hierarchy result = new Hierarchy();
        ArrayList<Branch> content = new ArrayList<>();

        //For each branch
        for (Branch b : h.getBranches()) {

            //Get collection
            DBCollection coll = db.getCollection(file);

            //Create new object to filter docs
            System.out.println("New object");
            BasicDBObject query = new BasicDBObject();

            //For each column
            Iterator it = b.getData().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                //headers.add(pair.getKey().toString());

                //For each value
                for (String value : b.getData().get(pair.getKey())) {
                    query.put(pair.getKey().toString(), value);
                    System.out.println(pair.getKey().toString() + " = " + value);
                }
            }

            //Do the query
            DBCursor cursor = coll.find(query);
            int i = 1;

            //Create new collection for each branch
            if (db.collectionExists(file + bIndex)) {
                coll = db.getCollection(file + bIndex);
                coll.drop();
            }
            coll = db.createCollection(file + bIndex, null);

            while (cursor.hasNext()) {
                System.out.println("Inserted Document: " + i);
                BasicDBObject doc = (BasicDBObject) cursor.next();
                headers = new ArrayList<>(doc.keySet());
                System.out.println(doc);
                MongoJDBC.insert(coll, doc);
                i++;
            }
            bIndex++;

            //Get unique values
            HashMap<String, ArrayList<String>> data = new HashMap<>();

            //Create index for each field
            if (!headers.isEmpty()) {
                headers.remove(0);
                for (String header : headers) {
                    coll.createIndex(header);
                    data.put(header, (ArrayList<String>) coll.distinct(header));
                }

                Branch temp = new Branch(data);
                content.add(temp);
            }
        }


        //Return result
        result.setBranches(content);
        return result;

    }
}

