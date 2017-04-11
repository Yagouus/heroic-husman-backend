package hello.parser;


import hello.dataTypes.Headers;
import hello.storage.StorageService;

import java.io.*;

import java.util.ArrayList;

public class parserCSV {

    /*public static void parse(String file) {

        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] columns = line.split(cvsSplitBy);

                System.out.println(columns[0] + columns[1] + columns[2] + columns[3] + columns[4] + columns[5] + columns[6]);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }*/

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

    public static void removeColumns(String file, Headers headers, StorageService storageService) {

        //Trim uri to file name
        String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());


        //Load file and get path
        String filePath = storageService.load(fileName).toString();
        String archivePath = filePath.replace(fileName, "");

        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String fileExt = file.substring(file.lastIndexOf("."), file.length());

        System.out.println(fileName);
        System.out.println(filePath);
        System.out.println(archivePath);

        try{
            PrintWriter writer = new PrintWriter(archivePath + fileName + "parsed" + fileExt, "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();
        } catch (IOException e) {
            // do something
        }


        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            //If file is not empty
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(cvsSplitBy);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}