package logEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import domainLogic.exceptions.*;
import logEditor.dataTypes.Headers;
import logEditor.dataTypes.Hierarchy;
import logEditor.dataTypes.LogFile;
import logEditor.parser.parserCSV;
import logEditor.persistence.MongoDAO;
import logEditor.storage.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import logEditor.storage.StorageService;

@RestController
public class FileController {

    private static final String template = "Your file is, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
        parserCSV.storageService = storageService;
        LogService.storageService = storageService;
    }

    //Lists all files in the server
    @CrossOrigin
    @GetMapping("/archivos")
    public ArrayList<LogFile> listUploadedFiles(Model model) throws IOException {
        return LogService.getLogFiles();
    }

    //Lists all files in the server
    @CrossOrigin
    @RequestMapping(value = "/logs/{id:.+}", method = RequestMethod.DELETE)
    public void deleteLog(@PathVariable("id") String id) throws IOException {
        LogService.deleteLog(LogService.getLogByName(id));
    }

    //Lists all files in the server
    @CrossOrigin
    @GetMapping("/logs")
    public ArrayList<LogFile> listUploadedLogs(Model model) throws IOException {
        return LogService.getLogFiles();
    }


    //Lists all files in the server
    @CrossOrigin
    @GetMapping("/dbs")
    public Set<String> listDataBases(Model model) throws IOException {
        return MongoDAO.getCollections();
    }

    //Saves file to the server and registers LogFile in collection
    @CrossOrigin
    @RequestMapping("/fileUpload")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file, String name) {
        storageService.store(file);
        LogService.insertLog(name, file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Returns log column headers
    @CrossOrigin
    @RequestMapping("/headers")
    public Headers listFileHeaders(@RequestParam("file") String file) {
        return LogService.getLogByName(file).getHeaders();
    }

    //Removes the non selected columns from a log
    @CrossOrigin
    @RequestMapping(value = "/filterLog", method = RequestMethod.POST)
    public Headers filterLog(@RequestParam("file") String file, Headers headers) {
        LogService.getLogByName(file).insertFile(headers);
        return LogService.getLogByName(file).getHeaders();
        //return parserCSV.removeColumns(file, headers, storageService);
    }

    //Sets the columns used to create hierarchies
    @CrossOrigin
    @RequestMapping(value = "/hierarchyCols", method = RequestMethod.POST)
    public void setHierarchyCols(@RequestParam("file") String file, Headers headers) {
        LogService.getLogByName(file).setHierarchyCols(headers);
    }

    //Sets the trace, activity and timestamp columns
    @CrossOrigin
    @RequestMapping(value = "/activityCol", method = RequestMethod.POST)
    public void setActIdTime(@RequestParam("file") String file, String trace, String act, String timestamp) {
        LogService.getLogByName(file).setTraceActTime(trace, act, timestamp);
        //return parserCSV.removeColumns(file, headers, storageService);
    }

    //Returns the uniques of the columns to filter a log
    @CrossOrigin
    @RequestMapping(value = "/nulls", method = RequestMethod.POST)
    public Headers replaceNulls(@RequestParam("file") String file, String column, String value) {
        LogService.getLogByName(file).replaceNulls(column, value);
        return LogService.getLogByName(file).getHeaders();
    }

    //Replaces the null values of a column with a determined value
    @CrossOrigin
    @RequestMapping(value = "/db", method = RequestMethod.GET)
    public HashMap<String, ArrayList<String>> db(@RequestParam("db") String db) {
        return LogService.getLogByName(db).UniquesToFilter();
    }

    //Returns the uniques of the columns to filter a log
    @CrossOrigin
    @RequestMapping(value = "/replaceValues", method = RequestMethod.POST)
    public HashMap<String, ArrayList<String>> replaceValues(@RequestParam("file") String file, String column, Headers values, String replacement) {
        LogService.getLogByName(file).replaceValues(column, values.getData(), replacement);
        return LogService.getLogByName(file).UniquesToFilter();
    }

    //Queries a log with a determined hierarchy
    @CrossOrigin
    @RequestMapping(value = "/hierarchy", method = RequestMethod.POST)
    public Hierarchy hierarchy(@RequestParam("file") String file, Hierarchy hierarchies) throws EmptyLogException, WrongLogEntryException, MalformedFileException, NonFinishedWorkflowException, InvalidFileExtensionException {

        hierarchies.getBranches();
        return MongoDAO.queryLog(LogService.getLogByName(file), hierarchies, storageService);

    }
}

