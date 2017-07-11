package logEditor.persistence;

import com.mongodb.*;

import domainLogic.exceptions.*;
import domainLogic.workflow.CaseInstance;
import domainLogic.workflow.Log;
import domainLogic.workflow.LogEntryInterface;
import domainLogic.workflow.algorithms.geneticMining.individual.CMIndividual;
import domainLogic.workflow.algorithms.heuristic.heuristicsminer.HeuristicsMiner;
import domainLogic.workflow.algorithms.heuristic.settings.HeuristicsMinerSettings;
import domainLogic.workflow.logReader.LogReaderCSV;
import domainLogic.workflow.logReader.LogReaderInterface;
import logEditor.dataTypes.Branch;
import logEditor.dataTypes.Headers;
import logEditor.dataTypes.Hierarchy;
import logEditor.dataTypes.LogFile;
import logEditor.parser.parserCSV;
import logEditor.storage.LogService;
import logEditor.storage.StorageService;

import java.io.*;
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

    public static DBCursor queryCollection(DBCollection coll, BasicDBObject query) {
        return coll.find(query);
    }

    public static DBCollection getCollection(String name) {
        return db.getCollection(name);
    }

    public static Set<String> getCollections() {
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

                    doc.append(headers.get(i), data.replace("\"", ""));
                }
                MongoJDBC.insert(coll, doc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static HashMap<String, ArrayList<String>> getContent(String collName, Headers h) {

        ArrayList<String> headers = h.getData();

        DBCollection coll = db.getCollection(collName);

        //Get unique values
        HashMap<String, ArrayList<String>> data = new HashMap<>();

        List<DBObject> indexes = coll.getIndexInfo();

        //Create index for each field
        for (int i = 1; i < indexes.size(); i++) {
            String key = indexes.get(i).get("key").toString();
            String[] tokens = key.split("\"");
            if (headers != null) {
                if (headers.contains(tokens[1])) {
                    data.put(tokens[1], (ArrayList<String>) coll.distinct(tokens[1]));
                }
            } else {
                data.put(tokens[1], (ArrayList<String>) coll.distinct(tokens[1]));
            }
        }

        return data;

    }

    public static Hierarchy queryLog(LogFile logFile, Hierarchy h, StorageService storageService) throws EmptyLogException, WrongLogEntryException, MalformedFileException, NonFinishedWorkflowException, InvalidFileExtensionException {

        String file = logFile.getName();


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
            BasicDBObject query = new BasicDBObject();

            //For each column
            Iterator it = b.getData().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                //headers.add(pair.getKey().toString());

                //For each value
                List<String> list = new ArrayList<>();
                for (String value : b.getData().get(pair.getKey())) {
                    list.add(value);

                }
                query.put(pair.getKey().toString(), new BasicDBObject("$in", list));
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

            //Insert documents
            while (cursor.hasNext()) {
                BasicDBObject doc = (BasicDBObject) cursor.next();
                headers = new ArrayList<>(doc.keySet());
                MongoJDBC.insert(coll, doc);
                i++;
            }
            bIndex++;

            //Get unique values
            //HashMap<String, ArrayList<String>> data = new HashMap<>();

            //Rename fields with id, trace and timestamp
            BasicDBObject doc = new BasicDBObject();
            Iterator mi = logFile.getPairing().entrySet().iterator();
            while (mi.hasNext()) {
                Map.Entry pair = (Map.Entry) mi.next();

                //Add new value to the object
                doc.append("$rename", new BasicDBObject().append(pair.getValue().toString(), pair.getKey().toString()));
                coll.updateMulti(query, doc);
            }

            doc = new BasicDBObject();

            //Update collection
            mi = logFile.getPairing().entrySet().iterator();

            //Create index for each field
            while (mi.hasNext()) {
                Map.Entry pair = (Map.Entry) mi.next();
                coll.createIndex(pair.getKey().toString());
                //data.put(pair.getKey().toString(), (ArrayList<String>) coll.distinct(pair.getValue().toString()));
            }

            cursor = coll.find();

            printToCSV(coll);

            LogReaderInterface reader = new LogReaderCSV();
            File f = new File(coll.getName());

            ArrayList<LogEntryInterface> entries = reader.read(null, null, f);

            Log l = new Log("log", "path", entries);
            System.out.println();

            /*ArrayList<CaseInstance> instances = new ArrayList<>();
            while (cursor.hasNext()) {
                DBObject trace = cursor.next();
                CaseInstance c = new CaseInstance();
                c.setId(trace.get("trace").toString());
                while (cursor.hasNext() && cursor.next().get("trace").toString().equals(trace.get("trace").toString())) {
                    c.addToTaskSequence(Integer.parseInt(cursor.curr().get("activity").toString()));
                }
                instances.add(c);
            }

            //Set instances
            l.setCaseInstances(instances);*/

            //Add tasks

            //Call heuristic minner

            HeuristicsMiner hm = new HeuristicsMiner(l, new HeuristicsMinerSettings());
            CMIndividual individual = hm.mine();
            individual.print();
        }


        //Return result
        result.setBranches(content);


        return result;

    }

    public static void replaceNulls(String collName, String column, String value) {

        //Create object with the query
        BasicDBObject query = new BasicDBObject();
        query.put(column, "-");

        //Get the collection
        DBCollection coll = db.getCollection(collName);

        //Add new value to the object
        BasicDBObject doc = new BasicDBObject();
        doc.append("$set", new BasicDBObject().append(column, value));

        //Update collection
        coll.updateMulti(query, doc);

    }

    public static void replaceValues(String collName, String column, ArrayList<String> values, String replacement) {

        //Get the collection
        DBCollection coll = db.getCollection(collName);

        for (String value : values) {

            //Create object with the query
            BasicDBObject query = new BasicDBObject();
            query.put(column, value);

            //Add new value to the object
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(column, replacement));

            //Update collection
            coll.updateMulti(query, doc);
        }
    }

    public static void dropColl(String collName) {
        DBCollection coll = db.getCollection(collName);
        coll.drop();
    }

    private static void printToCSV(DBCollection coll) {

        //Create file writer
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(coll.getName(), "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Create cursor
        DBCursor cursor = coll.find();

        //Write header
        writer.print("CaseIdentifier, TaskIdentifier\n");

        //Iterate results
        while (cursor.hasNext()) {
            DBObject trace = cursor.next();
            //System.out.println(trace);
            writer.print(trace.get("trace") + ",");
            writer.print(trace.get("activity") + "\n");
            //writer.print(trace.get("timestamp")+ "\n");
            //writer.print(":complete\n");
        }

        writer.print("\n");
        writer.close();

    }
}


