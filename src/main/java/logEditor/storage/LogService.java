package logEditor.storage;

import logEditor.dataTypes.LogFile;
import logEditor.persistence.LogRepository;

import java.io.File;
import java.util.ArrayList;

public class LogService {

    private static LogRepository repo;
    public static StorageService storageService;
    private static ArrayList<LogFile> logFiles = new ArrayList<>();

    public static void init(LogRepository repository) {
        //Init repo
        repo = repository;

        //Select all files from collection
        logFiles = (ArrayList<LogFile>) repo.findAll();
    }

    public static LogFile insertLog(String name, String path) {
        //Insert log
        repo.save(new LogFile(null, name, path, name, null, null, null, null, null));

        //Return created log
        return null;
    }

    public static void deleteLog(LogFile logFile) {

        //Delete file
        File file = new File(storageService.load(logFile.getPath()).toString());

        if (file.delete()) {
            System.out.println(file.getName() + " is deleted!");
        } else {
            System.out.println("File not found");
        }

        //Delete MongoDB Coll
        logFile.dropColl();

        //Delete logFile from coll
        repo.delete(logFile);

        System.out.println("LOG DELETED");
    }

    public static ArrayList<LogFile> getLogFiles() {
        return logFiles = (ArrayList<LogFile>) repo.findAll();
    }

    public static LogFile getLogByName(String name) {
        return repo.findByName(name);
    }

    public static LogFile save(LogFile logFile) {
        return repo.save(logFile);
    }

}
