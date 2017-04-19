package hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import hello.dataTypes.Headers;
import hello.parser.parserCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import hello.storage.StorageService;

@RestController
public class FileController {

    private static final String template = "Your file is, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    //Lists all files in the server
    @GetMapping("/archivos")
    public List<String> listUploadedFiles(Model model) throws IOException {

        return storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList());

    }

    //Accepts a file and saves it to the server
    @RequestMapping("/fileUpload")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Returns log column headers
    @RequestMapping("/headers")
    public ArrayList<String> listFileHeaders(@RequestParam("file") String file) {
        //Trim uri to file name
        file = file.substring(file.lastIndexOf("/") + 1, file.length());
        //Load and parse file
        return parserCSV.getHeaders(storageService.load(file).toString());
    }

    //Removes the non selected columns from a log
    @RequestMapping(value = "/filterLog", method = RequestMethod.POST)
    public HashMap<String, ArrayList<String>> complexGreeting(@RequestParam("file") String file, Headers headers) {
        return parserCSV.removeColumns(file, headers, storageService);
    }


}

