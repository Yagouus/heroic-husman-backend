package hello.persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hello.dataTypes.Branch;
import hello.dataTypes.Hierarchy;
import hello.parser.parserCSV;
import hello.storage.StorageService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MongoDAO {

    private static MongoJDBC mongo = new MongoJDBC();

    public static void insertLog(String filePath, StorageService storageService) {

        //Create new collection
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        DBCollection coll;

        //If coll exists, override
        if (mongo.db.collectionExists(fileName)) {
            coll = mongo.db.getCollection(fileName);
            coll.drop();
        }
        coll = mongo.db.createCollection(fileName, null);

        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> headers = parserCSV.getHeaders(filePath);
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

        DBCollection coll = mongo.db.getCollection(collName);

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

    public static void queryLog(String file, Hierarchy h, StorageService storageService) {

        //Get collection
        DBCollection coll = mongo.db.getCollection(file);

        //For each branch
        for (Branch b : h.getBranches()) {

            //Create new object to filter docs
            System.out.println("New object");
            BasicDBObject query = new BasicDBObject();

            //For each column
            Iterator it = b.getData().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                //For each value
                for(String value : b.getData().get(pair.getKey())){
                    query.put(pair.getKey().toString(), value);
                    System.out.println(pair.getKey().toString() + " = " + value);
                }
                //query.put(pair.getKey().toString(), b.getData().get(pair.getKey()).get(0));
                //System.out.println(pair.getKey().toString() + " = " + b.getData().get(pair.getKey()).get(0));
            }

            //Do the query
            DBCursor cursor = coll.find(query);
            int i = 1;

            while (cursor.hasNext()) {
                System.out.println("Inserted Document: " + i);
                System.out.println(cursor.next());
                i++;
            }
        }


    }
}
