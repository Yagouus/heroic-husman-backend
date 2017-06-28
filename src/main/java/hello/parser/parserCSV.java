package hello.parser;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import hello.dataTypes.Headers;
import hello.dataTypes.Log;
import hello.persistence.MongoJDBC;
import hello.storage.StorageService;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;

public class parserCSV {

    private static MongoJDBC mongo = new MongoJDBC();
    public static StorageService storageService;

    public static Headers getHeaders(String file) {

        System.out.println("FILE PATH: " + file);

        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> result = new ArrayList<>();

        System.out.println("FILE PATH LOADED:" + storageService.load(file).toString());

        try (BufferedReader br = new BufferedReader(new FileReader(storageService.load(file).toString()))) {

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

        Headers h = new Headers();
        h.setData(result);
        return h;
    }

    public static HashMap<String, ArrayList<String>> removeColumns(Log log, Headers headers) {

        String file = log.getPath();
        String name = log.getName();

        //Load file and get path
        String filePath = storageService.load(file).toString();
        String archivePath = filePath.replace(file, "");

        //Get filename and create new fileName
        String f = file.substring(0, file.lastIndexOf('.'));
        String fileExt = file.substring(file.lastIndexOf("."), file.length());
        String newFilePath = archivePath + f + "Parsed" + fileExt;
        log.setPath(f + "Parsed" + fileExt);
        //String newFilePath = archivePath + file + fileExt;

        //Create new file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(newFilePath, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Create Mongo collection
        DBCollection coll;
        if (MongoJDBC.db.collectionExists(name)) {
            coll = MongoJDBC.db.getCollection(name);
            coll.drop();
        }


        coll = MongoJDBC.db.createCollection(name, null);


        //Get headers to delete indexes
        ArrayList<Integer> indexes = new ArrayList<>();
        ArrayList<String> originalHeaders = log.getHeaders().getData();
        //ArrayList<String> newHeaders = new ArrayList<>();
        for (String header : headers.getData()) {
            if (originalHeaders.contains(header)) {
                indexes.add(originalHeaders.indexOf(header));
                //newHeaders.add(originalHeaders.get(originalHeaders.indexOf(header)));
            }
        }

        //Rewrite new file
        String line = "";
        ArrayList<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            //Headers
            if ((line = br.readLine()) != null) {

                //For each column of the file
                String[] columns = line.split(",");
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
                String[] columns = line.split(",");
                for (int i = 0; i < columns.length; i++) {
                    if (indexes.contains(i)) {

                        //Write to new file
                        writer.print(columns[i]);

                        //Add to mongo doc and remove quotes
                        String data = columns[i];
                        data = data.replace("\"", "");

                        //If column is empty, add a dash
                        if (!data.equals("")) {
                            doc.append(originalHeaders.get(i), data);
                        }else{
                            doc.append(originalHeaders.get(i), "-");
                        }

                        //Add a colon after each column
                        if (i < columns.length - 1) {
                            writer.print(",");
                        }
                    }
                }

                //Insert Mongo doc
                MongoJDBC.insert(coll, doc);
                if(doc.getString("PER_COD").equals("5145879")){
                    System.out.println(doc);
                }
                writer.print("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get unique values
        HashMap<String, ArrayList<String>> data = new HashMap<>();

        //Create index for each field
        for (String header : headers.getData()) {
            coll.createIndex(header);
            data.put(header, (ArrayList<String>) coll.distinct(header));
        }

        //delete old rename new
        storageService.load(file).toFile().delete();
        //File n = new File(file);
        //storageService.load(newFilePath).toFile().renameTo(n);


        //return data;
        return null;

        //MongoDAO.insertLog(newFilePath, storageService);

    }

}