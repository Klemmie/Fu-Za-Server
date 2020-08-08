package co.za.turtletech.fuzaserver.rest.impl;

import co.za.turtletech.fuzaserver.model.Users;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.model.Watched;
import co.za.turtletech.fuzaserver.persistance.UsersRepository;
import co.za.turtletech.fuzaserver.persistance.VideoRepository;
import co.za.turtletech.fuzaserver.persistance.WatchedRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FuZaRepositoryImpl {
    final UsersRepository usersRepository;
    final VideoRepository videoRepository;
    final WatchedRepository watchedRepository;

    public FuZaRepositoryImpl(UsersRepository usersRepository, VideoRepository videoRepository,
                              WatchedRepository watchedRepository) {
        this.usersRepository = usersRepository;
        this.videoRepository = videoRepository;
        this.watchedRepository = watchedRepository;
    }

    public List<Users> getAllUsersForCompany(String companyName) {
        return usersRepository.findAllByCompanyName(companyName);
    }

    public List<String> getAllCompanyNames() {
        List<Users> all = usersRepository.findAll();
        List<String> companyNames = new ArrayList<>();
        for (Users users : all) {
            if (!companyNames.contains(users.getCompanyName()))
                companyNames.add(users.getCompanyName());
        }
        return companyNames;
    }

    public List<Users> getAllUsersByRegisteredCourse(String registeredCourse) {
        return usersRepository.findAllByRegisteredCoursesContains(registeredCourse);
    }

    public Users getUserByCellNumber(String cellNumber) {
        return usersRepository.findByCellNumber(cellNumber);
    }

    public Users saveNewUser(Users newUser) {
        Users userByCellNumber = getUserByCellNumber(newUser.getCellNumber());
        if (userByCellNumber != null) {
            newUser.setCellNumber("0000000000");
            newUser.setCompanyName("*****");
            return newUser;
        }

        usersRepository.save(newUser);
        return newUser;
    }

    public Users updateUser(Users userUpdate) {
        Users currentUser = getUserByCellNumber(userUpdate.getCellNumber());
        if (currentUser != null) {
            currentUser.setCompanyName(userUpdate.getCompanyName());
            currentUser.setCellNumber(userUpdate.getCellNumber());
            currentUser.setRegisteredCourses(userUpdate.getRegisteredCourses());
            currentUser.setDeviceType(userUpdate.getDeviceType());
            usersRepository.save(currentUser);
            return userUpdate;
        }

        return null;
    }

    public Watched updateWatchedVideos(String cellNumber, String guid, String val, String watchedDateTime) {
        Video videoByGuid = getVideoByGuid(guid);
        Watched watched = new Watched();
        watched.setCellNumber(cellNumber);
        watched.setVideoName(videoByGuid.getName());
        watched.setWatched((val == null) ? "true" : "false");
        watched.setDate((watchedDateTime == null) ? LocalDateTime.now() : LocalDateTime.parse(watchedDateTime));

        List<Watched> byCellNumber = getAllWatchedVideosForUser(cellNumber);
        boolean save = true;
        for (Watched storedWatched : byCellNumber) {
            if (watched.getVideoName().equals(storedWatched.getVideoName())) {
                save = false;
                break;
            }
        }
        if (save)
            watchedRepository.save(watched);
        return watched;
    }

    public Watched removeWatched(String cellNumber, String guid) {
        Video videoByGuid = getVideoByGuid(guid);
        Watched watched = new Watched();
        watched.setCellNumber(cellNumber);
        watched.setVideoName(videoByGuid.getName());

        watchedRepository.delete(watched);

        return watched;
    }

    public List<Watched> getAllWatchedVideosForUser(String cellNumber) {
        return watchedRepository.findByCellNumber(cellNumber);
    }

    public void insertNewVideoUpload(Video video) {
        List<Video> videoOnCourse = getVideoOnCourse(video.getCourse(), null);
        int order = 0;
        for (Video storedVideo : videoOnCourse) {
            if (storedVideo.getVidOrder() > order)
                order = storedVideo.getVidOrder();
        }

        if (getVideoByName(video.getName()) == null) {
            video.setVidOrder(order + 1);
            videoRepository.save(video);
        }
    }

    public Video getVideoByName(String name) {
        return videoRepository.findVideoByName(name);
    }

    public Video getVideoByGuid(String guid) {
        return videoRepository.findByGuid(guid);
    }

    public List<Video> getVideoOnCourse(String course, String cellNumber) {
        List<Video> allByCourse = videoRepository.findAllByCourse(course);
        List<Video> returnVideos = new ArrayList<>();

        if (cellNumber != null) {
            List<Watched> byCellNumber = getAllWatchedVideosForUser(cellNumber);

            for (Video video : allByCourse) {
                boolean add = true;
                for (Watched watched : byCellNumber) {
                    if (watched.getVideoName().equals(video.getName())) {
                        if (watched.getWatched().equals("true"))
                            add = false;
                        break;
                    }
                }
                if (add)
                    returnVideos.add(video);
            }

            return returnVideos;
        }
        return allByCourse;
    }
}
