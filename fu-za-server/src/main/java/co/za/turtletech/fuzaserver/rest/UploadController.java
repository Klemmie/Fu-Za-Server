package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import co.za.turtletech.fuzaserver.storage.StorageFileNotFoundException;
import co.za.turtletech.fuzaserver.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("upload")
public class UploadController {
    final FuZaRepositoryImpl fuZaRepository;
    final StorageService storageService;

    Logger logger = LoggerFactory.getLogger(UploadController.class);

    public UploadController(FuZaRepositoryImpl fuZaRepository, StorageService storageService) {
        this.fuZaRepository = fuZaRepository;
        this.storageService = storageService;
    }

    @GetMapping("/listUploadedFiles")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(UploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping(value = "/fileUpload/{course}/{videoName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fileUpload(@RequestParam("file") MultipartFile file,
                                        @PathVariable String course,
                                        @PathVariable String videoName) {

        storageService.store(file, course, videoName);
        Video retVideo = new Video();
        retVideo.setCourse(course);
        retVideo.setName(videoName);
        return ResponseEntity.status(200).body(retVideo);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
