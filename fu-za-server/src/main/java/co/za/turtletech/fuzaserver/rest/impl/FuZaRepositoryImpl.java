package co.za.turtletech.fuzaserver.rest.impl;

import co.za.turtletech.fuzaserver.model.DeviceContent;
import co.za.turtletech.fuzaserver.model.Syncing;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.model.Watched;
import co.za.turtletech.fuzaserver.persistance.DeviceContentRepository;
import co.za.turtletech.fuzaserver.persistance.SyncingRepository;
import co.za.turtletech.fuzaserver.persistance.VideoRepository;
import co.za.turtletech.fuzaserver.persistance.WatchedRepository;
import co.za.turtletech.fuzaserver.rest.FuZaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class FuZaRepositoryImpl {
    final SyncingRepository syncingRepository;
    final VideoRepository videoRepository;
    final WatchedRepository watchedRepository;
    final DeviceContentRepository deviceContentRepository;

    public FuZaRepositoryImpl(SyncingRepository syncingRepository, VideoRepository videoRepository,
                              WatchedRepository watchedRepository, DeviceContentRepository deviceContentRepository) {
        this.syncingRepository = syncingRepository;
        this.videoRepository = videoRepository;
        this.watchedRepository = watchedRepository;
        this.deviceContentRepository = deviceContentRepository;
    }

    public List<Syncing> getAllUsersForCompany(String companyName) {
        return syncingRepository.findAllByCompanyName(companyName);
    }

    public List<Syncing> getAllUsersByRegisteredCourse(String registeredCourse) {
        return syncingRepository.findAllByRegisteredCoursesContains(registeredCourse);
    }

    public Syncing getUserByCellNumber(String cellNumber) {
        return syncingRepository.findByCellNumber(cellNumber);
    }

    public Syncing getUserByAppRegistration(String appRegistrationId) {
        return syncingRepository.findByAppRegistrationId(appRegistrationId);
    }

    public Syncing saveNewUser(Syncing newUser) {
        Syncing userByCellNumber = getUserByCellNumber(newUser.getCellNumber());
        if (userByCellNumber != null) {
            newUser.setCellNumber("0000000000");
            newUser.setCompanyName("*****");
            return newUser;
        }

        syncingRepository.save(newUser);
        return newUser;
    }

    public Syncing updateUser(Syncing userUpdate) {
        Syncing currentUser = getUserByCellNumber(userUpdate.getCellNumber());
        if (currentUser != null) {
            currentUser.setCompanyName(userUpdate.getCompanyName());
            currentUser.setCellNumber(userUpdate.getCellNumber());
            currentUser.setAppRegistrationId(userUpdate.getAppRegistrationId());
            currentUser.setRegisteredCourses(userUpdate.getRegisteredCourses());
            currentUser.setDeviceType(userUpdate.getDeviceType());
            syncingRepository.save(currentUser);
            return userUpdate;
        }

        return null;
    }

    public Watched updateWatchedVideos(String cellNumber, String guid, String val) {
        Video videoByGuid = getVideoByGuid(guid);
        Watched watched = new Watched();
        watched.setCellNumber(cellNumber);
        watched.setVideoName(videoByGuid.getName());
        watched.setWatched(val);
        watched.setDate(LocalDate.now());

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

    public List<Watched> getAllWatchedVideosForUser(String cellNumber) {
        return watchedRepository.findByCellNumber(cellNumber);
    }

    public void insertNewVideoUpload(Video video) {
        List<Video> videoOnCourseAndLevel = getVideoOnCourseAndLevel(video.getCourse(), video.getLevel(), null);
        int order = 0;
        for (Video storedVideo : videoOnCourseAndLevel) {
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

    public List<DeviceContent> getCurrentContentOnDevice(String appRegistrationId) {
        return deviceContentRepository.findDeviceContentByAppRegistrationId(appRegistrationId);
    }

    public List<Video> getVideoOnCourseAndLevel(String course, String level, String cellNumber) {
        List<Video> allByCourseAndLevel = videoRepository.findAllByCourseAndLevel(course, level);
        List<Video> returnVideos = new ArrayList<>();

        if (cellNumber != null) {
            List<Watched> byCellNumber = getAllWatchedVideosForUser(cellNumber);

            for (Video video : allByCourseAndLevel) {
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
        return allByCourseAndLevel;
    }
}
