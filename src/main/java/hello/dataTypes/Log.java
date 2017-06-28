package hello.dataTypes;

import com.mongodb.DBCollection;
import hello.parser.parserCSV;
import hello.persistence.MongoDAO;
import hello.persistence.MongoJDBC;
import hello.storage.LogService;
import hello.storage.StorageService;
import org.springframework.data.annotation.Id;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Log {

    @Id
    public String id;

    Long user;
    String name;
    String path;
    String dbName;
    DBCollection coll;
    Headers headers;
    Headers hierarchyCols;
    HashMap<String, String> pairing;
    String state;

    //BUILDERS
    public Log() {
    }

    public Log(Long user, String name, String path, String dbName, DBCollection coll, Headers headers, Headers hierarchyCols, HashMap<String, String> pairing, String state) {

        //Assing data
        this.user = user;
        this.name = name;
        this.path = path;
        this.dbName = dbName;
        this.coll = coll;
        this.headers = headers;
        this.hierarchyCols = hierarchyCols;
        this.pairing = pairing;
        this.state = state;
    }

    //SETTERS
    public void setName(String name) {
        this.name = name;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setDb(String db) {
        this.dbName = db;
        this.coll = MongoDAO.getCollection(this.dbName);
    }
    public void setColl(DBCollection coll) {
        //this.coll = coll;
        LogService.save(this);
    }
    public void setHierarchyCols(Headers h) {
        this.hierarchyCols = h;
        LogService.save(this);
    }
    public void setState(String state) {
        this.state = state;
    }

    //GETTERS
    public String getName() {
        return this.name;
    }
    public String getPath() {
        return this.path;
    }
    public DBCollection getColl() {
        return this.coll;
    }
    public Headers getHeaders() {

        //Check if headers already fetched
        if (this.headers == null) {
            this.headers = parserCSV.getHeaders(this.path);
            LogService.save(this);
        }

        return this.headers;
    }
    public String getState() {
        return state;
    }
    public HashMap<String, String> getPairing() {
        return pairing;
    }
    public Headers getHierarchyCols() {
        return hierarchyCols;
    }
    public Long getUser() {
        return user;
    }

    //Custom funcs
    public HashMap<String, ArrayList<String>> insertFile(Headers columns) {

        this.setState("Processing");
        LogService.save(this);

        HashMap<String, ArrayList<String>> r = parserCSV.removeColumns(this, columns);
        this.headers = parserCSV.getHeaders(this.path);
        this.setState("loaded");
        LogService.save(this);


        return r;
    }


    public void setTraceActTime(String trace, String act, String timestamp) {
        this.pairing = new HashMap<>();
        pairing.put("trace", trace);
        pairing.put("activity", act);
        pairing.put("timestamp", timestamp);
        LogService.save(this);
    }

    public HashMap<String, ArrayList<String>> UniquesToFilter() {
        return MongoDAO.getContent(this.name, this.hierarchyCols);
    }

    public void replaceNulls(String column, String value) {
        MongoDAO.replaceNulls(this.getName(), column, value);
    }

    public void replaceValues(String column, ArrayList<String> values, String replacement) {
        MongoDAO.replaceValues(this.getName(), column, values, replacement);
    }

    private void setState(){
        if(this.getHierarchyCols() != null){
            this.setState("processing");

        }
    }

    public void dropColl(){
        MongoDAO.dropColl(this.name);
    }
}

