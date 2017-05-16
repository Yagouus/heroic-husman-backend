package hello.storage;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hello.dataTypes.Log;
import hello.persistence.LogRepository;
import hello.persistence.MongoDAO;

import java.util.ArrayList;

public class LogService {

    private static LogRepository repo;
    private static DBCollection storage;
    private static ArrayList<Log> logs = new ArrayList<>();

    public static void init(LogRepository repository) {
        //Init repo
        repo = repository;

        //Select all files from collection
        logs = (ArrayList<Log>) repo.findAll();
    }

    public static Log insertLog(String name, String path) {
        //Insert log
        repo.save(new Log(null, name, path, name, null,null, null, null, null));

        //Return created log
        return null;
    }

    public static ArrayList<Log> getLogs(){
        return logs = (ArrayList<Log>) repo.findAll();
    }

    public static Log getLogByName(String name){
        return repo.findByName(name);
    }

    public static Log save(Log log){
        return repo.save(log);
    }

}
