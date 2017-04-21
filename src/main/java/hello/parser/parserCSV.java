package hello.parser;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import hello.dataTypes.Headers;
import hello.persistence.MongoJDBC;
import hello.storage.StorageService;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;

public class parserCSV {

    private static MongoJDBC mongo = new MongoJDBC();

    public static ArrayList<String> getHeaders(String file) {

        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            //If file is not empty
            if ((line = br.readLine()) != null) {
                String[] columns = line.split(cvsSplitBy);

                //Add headers to array and remove quotes
                for (String header : columns)
                    result.add(header.replace("\"", ""));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, ArrayList<String>> removeColumns(String file, Headers headers, StorageService storageService) {

        //Trim uri to file name
        String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());

        //Load file and get path
        String filePath = storageService.load(fileName).toString();
        String archivePath = filePath.replace(fileName, "");

        //Get filename and create new fileName
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String fileExt = file.substring(file.lastIndexOf("."), file.length());
        String newFilePath = archivePath + fileName + "Parsed" + fileExt;

        //Create new file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(newFilePath, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Create Mongo collection
        DBCollection coll;
        if (mongo.db.collectionExists(fileName)) {
            coll = mongo.db.getCollection(fileName);
            coll.drop();
        }
        coll = mongo.db.createCollection(fileName, null);

        //Get headers to delete indexes
        ArrayList<Integer> indexes = new ArrayList<>();
        ArrayList<String> originalHeaders = getHeaders(filePath);
        ArrayList<String> newHeaders = new ArrayList<>();
        for (String header : headers.getData()) {
            if (originalHeaders.contains(header)) {
                indexes.add(originalHeaders.indexOf(header));
                newHeaders.add(originalHeaders.get(originalHeaders.indexOf(header)));
            }
        }

        //Rewrite new file
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            //We skip headers in DB
            if ((line = br.readLine()) != null) {

                //For each column of the file
                String[] columns = line.split(cvsSplitBy);
                for (int i = 0; i < columns.length; i++) {
                    if (indexes.contains(i)) {
                        //Write to new file
                        writer.print(columns[i]);

                        if (i < columns.length - 1) {
                            writer.print(",");
                        }
                    }
                }

                writer.print("\n");
            }

            //For each line of the file
            while ((line = br.readLine()) != null) {

                //Create doc
                BasicDBObject doc = new BasicDBObject();

                //For each column of the file
                String[] columns = line.split(cvsSplitBy);
                for (int i = 0; i < columns.length; i++) {
                    if (indexes.contains(i)) {

                        //Write to new file
                        writer.print(columns[i]);

                        //Add to mongo doc
                        String data = columns[i];
                        data = data.replace("\"", "");

                        if (!data.equals("")) {
                            doc.append(originalHeaders.get(i), data);
                        }else{
                            doc.append(originalHeaders.get(i), "-");
                        }

                        if (i < columns.length - 1) {
                            writer.print(",");
                        }
                    }
                }

                //Insert Mongo doc
                MongoJDBC.insert(coll, doc);
                writer.print("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get unique values
        HashMap<String, ArrayList<String>> data = new HashMap<>();

        //Create index for each field
        for (String header : newHeaders) {
            coll.createIndex(header);
            data.put(header, (ArrayList<String>) coll.distinct(header));
        }

        return data;


        //MongoDAO.insertLog(newFilePath, storageService);

    }

}