package hello;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import hello.parser.parserCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import hello.storage.StorageService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class FileController {

    private static final String template = "Your file is, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

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

    @RequestMapping("/fileUpload")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping("/headers")
    public void listFileHeaders(@RequestParam("file") String file) {
        file = file.substring(file.lastIndexOf("/") + 1, file.length());
        parserCSV.parse(storageService.load(file).toString());
    }

}

