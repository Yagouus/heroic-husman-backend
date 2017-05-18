package hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import hello.dataTypes.Headers;
import hello.dataTypes.Hierarchy;
import hello.dataTypes.Log;
import hello.parser.parserCSV;
import hello.persistence.MongoDAO;
import hello.storage.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import hello.storage.StorageService;

@RestController
public class FileController {

    private static final String template = "Your file is, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
        parserCSV.storageService = storageService;
    }

    //Lists all files in the server
    @GetMapping("/archivos")
    public ArrayList<Log> listUploadedFiles(Model model) throws IOException {

        /*return storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList());*/

        return LogService.getLogs();

    }

    //Lists all files in the server
    @GetMapping("/dbs")
    public Set<String> listDataBases(Model model) throws IOException {
        return MongoDAO.getCollections();
    }

    //Saves file to the server and registers Log in collection
    @RequestMapping("/fileUpload")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file, String name) {
        storageService.store(file);
        LogService.insertLog(name, file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Returns log column headers
    @RequestMapping("/headers")
    public Headers listFileHeaders(@RequestParam("file") String file) {

        //System.out.println("Get headers of: " + file);

        //Trim uri to file name
        //file = file.substring(file.lastIndexOf("/") + 1, file.length());
        //Load and parse file
        //System.out.println(file);
        return LogService.getLogByName(file).getHeaders();
        //return parserCSV.getHeaders(LogService.getLogByName(file).getPath());

    }

    //Removes the non selected columns from a log
    @RequestMapping(value = "/filterLog", method = RequestMethod.POST)
    public Headers filterLog(@RequestParam("file") String file, Headers headers) {
        LogService.getLogByName(file).insertFile(headers);
        return LogService.getLogByName(file).getHeaders();
        //return parserCSV.removeColumns(file, headers, storageService);
    }

    //Removes the non selected columns from a log
    @RequestMapping(value = "/hierarchyCols", method = RequestMethod.POST)
    public void setHierarchyCols(@RequestParam("file") String file, Headers headers) {
        System.out.println(headers.getData());
        LogService.getLogByName(file).setHierarchyCols(headers);
        //return parserCSV.removeColumns(file, headers, storageService);
    }


    //Removes the non selected columns from a log
    @RequestMapping(value = "/db", method = RequestMethod.GET)
    public HashMap<String, ArrayList<String>> db(@RequestParam("db") String db) {
        return MongoDAO.getContent(db);
    }

    //Removes the non selected columns from a log
    @RequestMapping(value = "/hierarchy", method = RequestMethod.POST)
    public Hierarchy hierarchy(@RequestParam("file") String file, Hierarchy hierarchies) {
        hierarchies.getBranches();
        return MongoDAO.queryLog(file, hierarchies, storageService);

    }
}

