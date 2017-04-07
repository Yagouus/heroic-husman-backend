package hello.parser;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class parserCSV {

    public static void parse(String file) {

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

    }

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


}