package hello.persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import hello.parser.parserCSV;
import hello.storage.StorageService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
}
