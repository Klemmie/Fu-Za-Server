package co.za.turtletech.fuzaserver.rest;

import co.za.turtletech.fuzaserver.model.Users;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("user")
public class UserController {
    final FuZaRepositoryImpl fuZaRepository;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
    }

    @PostMapping(value = "/newUser/{companyName}/{cellNumber}/{registeredCourses}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertNewUser(@PathVariable String companyName,
                                           @PathVariable String cellNumber,
                                           @PathVariable String registeredCourses) {
        Users newUser = new Users(companyName, cellNumber, registeredCourses, "Company", true);
        Users addedUser = fuZaRepository.saveNewUser(newUser);

        if (addedUser.getCellNumber().equals(newUser.getCellNumber())) {
            logger.info("New user " + newUser.getCellNumber() + " has been added");
            return ResponseEntity.status(201).body(newUser);
        }

        return ResponseEntity.status(409).body(addedUser);
    }

    @PostMapping(value = "/bulkAdd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bulkInsert(@RequestBody List<Users> users) {
        for (Users user : users) {
            Users save = fuZaRepository.saveNewUser(user);
            if (!save.getCellNumber().equals(user.getCellNumber()))
                return ResponseEntity.status(409).body("An error occurred");
        }

        return ResponseEntity.status(201).body("Users added");
    }

    @PutMapping(value = "/updatedUser/{companyName}/{cellNumber}/{registeredCourses}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable String companyName,
                                        @PathVariable String cellNumber,
                                        @PathVariable String registeredCourses) {
        Users userByCellNumber = fuZaRepository.getUserByCellNumber(cellNumber);
        String[] courses = userByCellNumber.getRegisteredCourses().split(",");
        String[] newCourses = registeredCourses.split(",");

        Set<String> combinedCourses = new HashSet<>();
        combinedCourses.addAll(Arrays.asList(courses));
        combinedCourses.addAll(Arrays.asList(newCourses));

        String allCourses = "";
        int size = 0;
        for (String combinedCourse : combinedCourses) {
            allCourses = allCourses.concat(combinedCourse);
            size++;
            if (size < combinedCourses.size())
                allCourses = allCourses.concat(",");
        }

        Users updatedUser = new Users(companyName, cellNumber, allCourses, userByCellNumber.getDeviceType(), true);

        Users addedUser = fuZaRepository.updateUser(updatedUser);

        if (addedUser.getCellNumber().equals(updatedUser.getCellNumber())) {
            logger.info("User " + updatedUser.getCellNumber() + " has been updated");
            return ResponseEntity.status(201).body(updatedUser);
        }

        return ResponseEntity.status(409).body(addedUser);
    }

    @PutMapping(value = "/removeUser/{cellNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeUser(@PathVariable String cellNumber){
        Users userByCellNumber = fuZaRepository.getUserByCellNumber(cellNumber);
        userByCellNumber.setActive(false);
        fuZaRepository.updateUser(userByCellNumber);

        return ResponseEntity.status(204).build();
    }

    @GetMapping(value = "/userDetails/{cellNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userDetails(@PathVariable String cellNumber) {
        Users user = fuZaRepository.getUserByCellNumber(cellNumber);
        logger.info("User (" + cellNumber + ") requested details");
        if (user != null)
            return ResponseEntity.status(200).body(user);

        return null;
    }
}
