package hello.storage;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hello.dataTypes.Log;
import hello.persistence.LogRepository;
import hello.persistence.MongoDAO;

import java.io.File;
import java.util.ArrayList;

public class LogService {

    private static LogRepository repo;
    public static StorageService storageService;
    private static ArrayList<Log> logs = new ArrayList<>();

    public static void init(LogRepository repository) {
        //Init repo
        repo = repository;

        //Select all files from collection
        logs = (ArrayList<Log>) repo.findAll();
    }

    public static Log insertLog(String name, String path) {
        //Insert log
        repo.save(new Log(null, name, path, name, null, null, null, null, null));

        //Return created log
        return null;
    }

    public static void deleteLog(Log log) {

        //Delete file
        File file = new File(storageService.load(log.getPath()).toString());

        if (file.delete()) {
            System.out.println(file.getName() + " is deleted!");
        } else {
            System.out.println("File not found");
        }

        //Delete MongoDB Coll
        log.dropColl();

        //Delete log from coll
        repo.delete(log);

        System.out.println("LOG DELETED");
    }

    public static ArrayList<Log> getLogs() {
        return logs = (ArrayList<Log>) repo.findAll();
    }

    public static Log getLogByName(String name) {
        return repo.findByName(name);
    }

    public static Log save(Log log) {
        return repo.save(log);
    }

}
