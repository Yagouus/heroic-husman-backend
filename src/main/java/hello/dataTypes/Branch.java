package hello.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class Branch {


    private HashMap<String, ArrayList<String>> data;

    public Branch(){
        this.data = new HashMap<>();
    }

    public Branch(HashMap<String, ArrayList<String>> data){
        this.data = data;
    }

    public void setData(HashMap<String, ArrayList<String>> data) {
        this.data = data;
    }

    public HashMap<String, ArrayList<String>> getData() {
        return data;
    }


}
