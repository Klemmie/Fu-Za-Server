package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Users;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.model.Watched;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import co.za.turtletech.fuzaserver.util.GeneratePdfReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("fu-za")
public class FuZaController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(FuZaController.class);

    public FuZaController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    @PostMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertNewUser(@RequestBody Users newUser) {
        Users addedUser = fuZaRepository.saveNewUser(newUser);

        if (addedUser.getCellNumber().equals(newUser.getCellNumber())) {
            logger.info("New user " + newUser.getCellNumber() + " has been added");
            return ResponseEntity.status(201).body(newUser);
        }

        return ResponseEntity.status(409).body(addedUser);
    }

    @PostMapping(value = "/bulkAdd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bulkInsert(@RequestBody List<Users> users){
        for (Users user : users) {
            Users save = fuZaRepository.saveNewUser(user);
            if(!save.getCellNumber().equals(user.getCellNumber()))
                return ResponseEntity.status(409).body("An error occurred");
        }

        return ResponseEntity.status(201).body("Users added");
    }

    @PutMapping(value = "/updatedUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody Users updatedUser) {
        Users addedUser = fuZaRepository.updateUser(updatedUser);

        if (addedUser.getCellNumber().equals(updatedUser.getCellNumber())) {
            logger.info("User " + updatedUser.getCellNumber() + " has been updated");
            return ResponseEntity.status(201).body(updatedUser);
        }

        return ResponseEntity.status(409).body(addedUser);
    }

    @GetMapping(value = "/companyNames", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCompanyNames(){
        return ResponseEntity.status(200).body(fuZaRepository.getAllCompanyNames());
    }

    @GetMapping(value = "/getLearnersByCompanyName/{companyName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllByCompanyName(@PathVariable String companyName){
        List<Users> allUsersForCompany = fuZaRepository.getAllUsersForCompany(companyName);

        return ResponseEntity.status(200).body(allUsersForCompany);
    }

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

    @PostMapping(value = "/addWatched/{cellNumber}/{guid}/{watched}/{watchedDateTime}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWatched(@PathVariable String cellNumber,
                                        @PathVariable String guid,
                                        @PathVariable(required = false) String watched,
                                        @PathVariable(required = false) String watchedDateTime) {

        Watched updateWatchedVideos = fuZaRepository.updateWatchedVideos(cellNumber, guid, watched, watchedDateTime);
        logger.info("User (" + cellNumber + ") watched video: " + guid);
        return ResponseEntity.status(201).body(updateWatchedVideos);
    }

    @GetMapping(value = "/getWatched/{cellNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWatched(@PathVariable String cellNumber){
        List<Watched> allWatchedVideosForUser = fuZaRepository.getAllWatchedVideosForUser(cellNumber);
        return ResponseEntity.status(200).body(allWatchedVideosForUser);
    }

    @PostMapping(value = "/removeWatched/{cellNumber}/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeWatched(@PathVariable String cellNumber,
                                           @PathVariable String guid){
        fuZaRepository.removeWatched(cellNumber, guid);
        return ResponseEntity.status(201).body("Record removed");
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

    @GetMapping(value = "/userDetails/{cellNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userDetails(@PathVariable String cellNumber) {
        Users user = fuZaRepository.getUserByCellNumber(cellNumber);
        logger.info("User (" + cellNumber + ") requested details");
        if (user != null)
            return ResponseEntity.status(200).body(user);

        return null;
    }

    @GetMapping(value = "/pdfReport/{company}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> watchedReport(@PathVariable String company) {
        List<Users> allUsersForCompany = fuZaRepository.getAllUsersForCompany(company);

        List<Watched> companyWatchList = new ArrayList<>();

        for (Users users : allUsersForCompany) {
            List<Watched> allWatchedVideosForUser = fuZaRepository.getAllWatchedVideosForUser(users.getCellNumber());
            String[] courses = users.getRegisteredCourses().split(",");
            for (String course : courses) {
                List<Video> videoOnCourse = fuZaRepository.getVideoOnCourse(course, null);
                for (Video video : videoOnCourse) {
                    boolean add = true;
                    for (Watched watched : allWatchedVideosForUser) {
                        if (video.getName().equals(watched.getVideoName())) {
                            companyWatchList.add(watched);
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        Watched watched = new Watched();
                        watched.setVideoName(video.getName());
                        watched.setCellNumber(users.getCellNumber());
                        watched.setDate(LocalDateTime.now());
                        watched.setWatched("false");
                        companyWatchList.add(watched);
                    }
                }
            }
        }

        ByteArrayInputStream bis = GeneratePdfReport.watchedReport(companyWatchList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "filename=" + company + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }


//    @GetMapping(value = "/pdfReport/{company}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> watchedReport(@PathVariable String company) {
//        List<Syncing> allUsersForCompany = fuZaRepository.getAllUsersForCompany(company);
//
//        List<Watched> companyWatchList = new ArrayList<>();
//
//        for (Syncing syncing : allUsersForCompany) {
//            List<Watched> allWatchedVideosForUser = fuZaRepository.getAllWatchedVideosForUser(syncing.getCellNumber());
//            String[] courses = syncing.getRegisteredCourses().split(",");
//            for (String course : courses) {
//                List<Video> videoOnCourseAndLevel = fuZaRepository.getVideoOnCourseAndLevel(course, "1", null);
//                for (Video video : videoOnCourseAndLevel) {
//                    boolean add = true;
//                    for (Watched watched : allWatchedVideosForUser) {
//                        if (video.getName().equals(watched.getVideoName())) {
//                            companyWatchList.add(watched);
//                            add = false;
//                            break;
//                        }
//                    }
//                    if (add) {
//                        Watched watched = new Watched();
//                        watched.setVideoName(video.getName());
//                        watched.setCellNumber(syncing.getCellNumber());
//                        watched.setDate(LocalDate.now());
//                        watched.setWatched("false");
//                        companyWatchList.add(watched);
//                    }
//                }
//            }
//        }
//        return ResponseEntity.ok().body(companyWatchList);
//    }
}
