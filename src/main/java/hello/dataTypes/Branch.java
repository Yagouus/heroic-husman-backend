package hello.dataTypes;

import java.util.ArrayList;

/**
 * Created by yagouus on 5/05/17.
 */
public class Branch {


    private ArrayList<String> data;

    public Branch(){}

    public Branch(ArrayList<String> data){
        this.data = data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }


    public ArrayList<String> getData() {
        return data;
    }


}
