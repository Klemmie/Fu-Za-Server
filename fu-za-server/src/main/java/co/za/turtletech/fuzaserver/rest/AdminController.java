package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.AdminScreenModel;
import co.za.turtletech.fuzaserver.model.Users;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.model.Watched;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import co.za.turtletech.fuzaserver.util.GeneratePdfReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    @GetMapping(value = "/adminScreenView/{companyName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAdminView(@PathVariable String companyName) {
        List<AdminScreenModel> frontendRepresentation = fuZaRepository.getFrontendRepresentation(companyName);

        return ResponseEntity.status(200).body(frontendRepresentation);
    }

    @GetMapping(value = "/companyNames", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCompanyNames() {
        return ResponseEntity.status(200).body(fuZaRepository.getAllCompanyNames());
    }

    @GetMapping(value = "/getLearnersByCompanyName/{companyName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllByCompanyName(@PathVariable String companyName) {
        List<Users> allUsersForCompany = fuZaRepository.getAllUsersForCompany(companyName);

        return ResponseEntity.status(200).body(allUsersForCompany);
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
