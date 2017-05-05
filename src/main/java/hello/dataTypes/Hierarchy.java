package hello.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class Hierarchy {

    private ArrayList<ArrayList<String>> data;

    public Hierarchy(){
    }

    public Hierarchy(ArrayList<ArrayList<String>> data){
        this.data = data;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }
}
