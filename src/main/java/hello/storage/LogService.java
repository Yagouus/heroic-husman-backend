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

        repo = repository;

        //Create a collection to store log information
        //storage = MongoDAO.createCollection("logs");

        //Init log repository

        //Select all files from collection
        repo.save(new Log());
        repo.findAll();



    }

    public static Log insertLog(String name, String path) {
        Log l = new Log();
        l.setName(name);
        l.setPath(path);
        repo.save(new Log());
        return l;
    }

    public static ArrayList<Log> getLogs(){
        return (ArrayList<Log>) repo.findAll();
    }

}
