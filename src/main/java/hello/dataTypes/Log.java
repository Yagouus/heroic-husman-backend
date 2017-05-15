package hello.dataTypes;

import com.mongodb.DBCollection;
import hello.persistence.MongoDAO;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;

public class Log {

    @Id
    public String id;

    private Long user;
    String name;
    String path;
    String dbName;
    DBCollection coll;
    Headers headers;
    Headers hierarchyCols;
    HashMap<String, String> pairing;
    String state;

    public Log (){};

    public void setName(String name){
        this.name = name;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setDb(String db){
        this.dbName = db;
        this.coll = MongoDAO.getCollection(this.dbName);
    }
}
