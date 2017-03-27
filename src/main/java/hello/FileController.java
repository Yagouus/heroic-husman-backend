package hello;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
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

    @RequestMapping(value="/imageUpload", method = RequestMethod.POST)
    public void UploadFile(MultipartHttpServletRequest request) throws IOException {

        Iterator<String> itr=request.getFileNames();
        MultipartFile file=request.getFile(itr.next());
        String fileName=file.getOriginalFilename();
        File dir = new File("C:\\file");
        if (dir.isDirectory())
        {
            File serverFile = new File(dir,fileName);
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(serverFile));
            stream.write(file.getBytes());
            stream.close();
        }else {
            System.out.println("not");
        }

    }
}

