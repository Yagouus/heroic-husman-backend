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

        //Get filename and create new fileName
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String fileExt = file.substring(file.lastIndexOf("."), file.length());

        //Create new file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(archivePath + fileName + "Parsed" + fileExt, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Get headers to delete indexes
        //PROBLEM AQUI
        ArrayList<Integer> indexes = new ArrayList<>();
        ArrayList<String> originalHeaders = getHeaders(filePath);
        System.out.println(originalHeaders);
        System.out.println(headers.getData());
        for (String header : headers.getData()) {
            if (originalHeaders.contains(header)) {
                System.out.println(header);
                System.out.println(originalHeaders.indexOf(header));
                indexes.add(originalHeaders.indexOf(header));
            }
        }

        System.out.println(indexes);

        //Rewrite new file
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            //For each line of the file
            while ((line = br.readLine()) != null) {

                //For each column of the file
                String[] columns = line.split(cvsSplitBy);
                for (int i = 0; i < columns.length; i++) {
                    if (!indexes.contains(i)) {
                        writer.print(columns[i]);

                        if (i < columns.length - 1) {
                            writer.print(",");
                        }
                    }

                }
                writer.print("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}