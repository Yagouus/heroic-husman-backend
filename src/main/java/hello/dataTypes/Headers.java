package hello.dataTypes;


import java.util.ArrayList;
import java.util.HashMap;

public class Headers {

    private ArrayList data;

    public Headers(){}

    public Headers(ArrayList<String> data){
        this.data = data;
    }

    public ArrayList getData() {
        return data;
    }

    public void setData(ArrayList data) {
        this.data = data;
    }
}
