package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Users;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("mobile")
public class MobileController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(MobileController.class);

    public MobileController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    //Mobile application endpoints
    @GetMapping(value = "/download/{cellNumber}/{guid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable String cellNumber,
                                             @PathVariable String guid) throws IOException {
        //83ffac9f-cb5b-4419-900a-d6ddb59a177b
        //a797eea9-f471-439d-a7f0-42bec9871124
        Users user = fuZaRepository.getUserByCellNumber(cellNumber);
        if (user != null) {
            Video videoByGuid = fuZaRepository.getVideoByGuid(guid);
            String[] courses = user.getRegisteredCourses().split(",");
            boolean valid = false;
            for (String course : courses) {
                if (course.equals(videoByGuid.getCourse())) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                //if (videoByGuid != null) {
                File file = new File(videoByGuid.getPath());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");

                Path path = Paths.get(file.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                logger.info("Download link generated for user: " + user.getCellNumber());
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
                //} else logger.error("Video could not be found by guid");
            } else logger.error("User not registered for course");
        } else logger.error("User cannot be null");

        return null;
    }

    @GetMapping(value = "/downloadPdf/{cellNumber}/{guid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadPdf(@PathVariable String cellNumber,
                                                @PathVariable String guid) throws ClassCastException, IOException {
        Video videoPdf = fuZaRepository.getVideoByGuid(guid);

        String filePath = videoPdf.getPath().substring(0, videoPdf.getPath().length() - 4);
        filePath = filePath + ".pdf";
        File file = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "filename=" + videoPdf.getName() + ".pdf");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        logger.info("PDF download link generated for user: " + cellNumber);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
