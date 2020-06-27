package co.za.turtletech.fuzaserver.rest.impl;

import co.za.turtletech.fuzaserver.model.Syncing;
import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.model.Watched;
import co.za.turtletech.fuzaserver.persistance.SyncingRepository;
import co.za.turtletech.fuzaserver.persistance.VideoRepository;
import co.za.turtletech.fuzaserver.persistance.WatchedRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FuZaRepositoryImpl {
    final SyncingRepository syncingRepository;
    final VideoRepository videoRepository;
    final WatchedRepository watchedRepository;

    public FuZaRepositoryImpl(SyncingRepository syncingRepository, VideoRepository videoRepository, WatchedRepository watchedRepository) {
        this.syncingRepository = syncingRepository;
        this.videoRepository = videoRepository;
        this.watchedRepository = watchedRepository;
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
            System.out.println("User already exists");
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
            System.out.println("User already exists");
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

    public Watched updateWatchedVideos(Watched watched) {
        return watchedRepository.save(watched);
    }

    public void insertNewVideoUpload(Video video) {
        if (getVideoByName(video.getName()) == null)
            videoRepository.save(video);
    }

    public Video getVideoByName(String name) {
        return videoRepository.findVideoByName(name);
    }

    public Video getVideoByGuid(String guid) {
        return videoRepository.findByGuid(guid);
    }
}
