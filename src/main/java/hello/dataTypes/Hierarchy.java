package hello.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class Hierarchy {

    //Raw data received from client
    //private ArrayList<ArrayList<String>> data;

    //Raw data
    private ArrayList<String> data;

    //Branches structured for correct iteration
    private ArrayList<Branch> branches;

    public Hierarchy() {
    }

    public Hierarchy(ArrayList<String> data) {
        this.data = data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public ArrayList<String> getData() {
        return data;
    }

    /*public ArrayList<Branch> getBranches() {

        //If they are not processed
        if (this.branches == null) {
            this.branches = new ArrayList<>();

            //Iterate over all branches
            for (int i = 0; i < this.data.size(); i++) {
                Branch temp = new Branch();
                System.out.println("---BRANCH---" + i);
                ArrayList<String> branch = this.data.get(i);

                //Iterate over the params of the branch
                for (String param : branch) {
                    System.out.print("new param: ");
                    //Split de raw string to get column and value
                    String[] columns = param.split(":");
                    String key = columns[0];
                    String value = columns[1];
                    System.out.print(key + " -> ");
                    System.out.println(value);

                    //Check if key exists
                    if (temp.getData().containsKey(key)) {
                        temp.getData().get(key).add(value);
                    } else {
                        ArrayList<String> row = new ArrayList<>();
                        row.add(value);
                        temp.getData().put(key, row);
                    }
                }

                branches.add(temp);

            }


            //If content has already been loaded
        }

        return this.branches;
    }

*/
    public ArrayList<Branch> getBranches() {

        //If they are not processed
        if (this.branches == null) {
            this.branches = new ArrayList<>();
            int bIndex = 0;

            for (String param : this.data) {

                //Split de raw string to get column and value
                String[] columns = param.split(":");
                Integer branch = Integer.parseInt(columns[0]);
                String key = columns[1];
                String value = columns[2];

                //If branch exists
                if (this.branches.size() <= branch) {
                    //System.out.println("---NEW BRANCH---");
                    this.branches.add(new Branch());
                }

                //System.out.print(key + " -> ");
                //System.out.println(value);

                //If key is registered
                if (this.branches.get(branch).getData().containsKey(key)) {
                    this.branches.get(branch).getData().get(key).add(value);
                } else {
                    //If key is not registered, create array and add with key
                    ArrayList<String> t = new ArrayList<>();
                    t.add(value);
                    this.branches.get(branch).getData().put(key, t);
                }
            }
            //If content has already been loaded
        }
        return this.branches;
    }
}
