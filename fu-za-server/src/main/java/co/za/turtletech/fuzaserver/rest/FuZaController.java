package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Syncing;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.model.Watched;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("fu-za")
public class FuZaController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(FuZaController.class);

    public FuZaController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    @PostMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertNewUser(@RequestBody Syncing newUser) {
        Syncing addedUser = fuZaRepository.saveNewUser(newUser);

        if (addedUser.getCellNumber().equals(newUser.getCellNumber())) {
            logger.info("New user " + newUser.getCellNumber() + " has been added");
            return ResponseEntity.status(201).body(newUser);
        }

        return ResponseEntity.status(409).body(addedUser);
    }

    @PutMapping(value = "/updatedUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody Syncing updatedUser) {
        Syncing addedUser = fuZaRepository.updateUser(updatedUser);

        if (addedUser.getCellNumber().equals(updatedUser.getCellNumber())) {
            logger.info("User " + updatedUser.getCellNumber() + " has been updated");
            return ResponseEntity.status(201).body(updatedUser);
        }

        return ResponseEntity.status(409).body(addedUser);
    }

    @GetMapping(value = "/download/{appRegistrationId}/{guid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable String appRegistrationId,
                                             @PathVariable String guid) throws IOException {
        //83ffac9f-cb5b-4419-900a-d6ddb59a177b
        //def9c179-a6a2-4c87-a2ca-52acbe94922c
        Syncing user = fuZaRepository.getUserByAppRegistration(appRegistrationId);
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

    @PostMapping(value = "/addWatched", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWatched(@RequestBody Watched watched) {
        Watched updateWatchedVideos = fuZaRepository.updateWatchedVideos(watched);
        logger.info("User (" + watched.getCellNumber() + ") watched video: " + watched.getVideoName());
        return ResponseEntity.status(201).body(updateWatchedVideos);
    }
}
