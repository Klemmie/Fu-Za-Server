package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Watched;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("watched")
public class WatchedController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(WatchedController.class);

    public WatchedController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    @PostMapping(value = "/addWatched/{cellNumber}/{guid}/{watched}/{watchedDateTime}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWatched(@PathVariable String cellNumber,
                                        @PathVariable String guid,
                                        @PathVariable(required = false) String watched,
                                        @PathVariable(required = false) String watchedDateTime) {
        logger.info("User: " + cellNumber + " video: " + guid + " watched: " + watched + " date+time: " + watchedDateTime);

        Watched updateWatchedVideos = fuZaRepository.updateWatchedVideos(cellNumber, guid, watched, watchedDateTime);
        logger.info("User (" + cellNumber + ") watched video: " + guid);
        return ResponseEntity.status(201).body(updateWatchedVideos);
    }

    @GetMapping(value = "/getWatched/{cellNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWatched(@PathVariable String cellNumber) {
        List<Watched> allWatchedVideosForUser = fuZaRepository.getAllWatchedVideosForUser(cellNumber);
        return ResponseEntity.status(200).body(allWatchedVideosForUser);
    }

    @PostMapping(value = "/removeWatched/{cellNumber}/{videoName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeWatched(@PathVariable String cellNumber,
                                           @PathVariable String videoName) {
        fuZaRepository.removeWatched(cellNumber, videoName);
        return ResponseEntity.status(201).body("Record removed");
    }
}
