package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Users;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("video")
public class VideoController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(VideoController.class);

    public VideoController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    @GetMapping(value = "/getVideoByName/{vidName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVideoByName(@PathVariable String vidName) {
        Video videoByName = fuZaRepository.getVideoByName(vidName);
        if (videoByName != null)
            return ResponseEntity.status(200).body(videoByName);

        return null;
    }

    @GetMapping(value = "/videoList/{course}/{cellNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> videoList(@PathVariable String course,
                                       @PathVariable String cellNumber) {
        Users user = fuZaRepository.getUserByCellNumber(cellNumber);
        if (user != null) {
            String[] courses = user.getRegisteredCourses().split(",");
            boolean valid = false;
            for (String userCourse : courses) {
                if (course.equals(userCourse)) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                logger.info("User (" + user.getCellNumber() + ") requested video list");
                List<Video> videoOnCourse = fuZaRepository.getVideoOnCourse(course, cellNumber);
                return ResponseEntity.status(200).body(videoOnCourse);
            }
            logger.error("User (" + cellNumber + ") not registered for course " + course);

        }
        logger.error("[GET VIDEO LIST] User cannot be null");

        return null;
    }
}
