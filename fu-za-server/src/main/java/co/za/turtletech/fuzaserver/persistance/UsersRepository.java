package co.za.turtletech.fuzaserver.persistance;

import co.za.turtletech.fuzaserver.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByCellNumber(String cellNumber);

    List<Users> findAllByRegisteredCoursesContains(String course);

    List<Users> findAllByCompanyName(String companyName);

    List<Users> findAll();
}
