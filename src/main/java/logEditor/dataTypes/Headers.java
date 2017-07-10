package logEditor.dataTypes;


import java.util.ArrayList;

public class Headers {

    private ArrayList<String> data;

    public Headers(){}

    public Headers(ArrayList<String> data){
        this.data = data;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }
}
