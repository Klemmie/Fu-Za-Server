package co.za.turtletech.fuzaserver.persistance;

import co.za.turtletech.fuzaserver.model.Syncing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncingRepository extends JpaRepository<Syncing, Long> {
    Syncing findByCellNumber(String cellNumber);

    Syncing findByAppRegistrationId(String appRegistrationId);

    List<Syncing> findAllByRegisteredCoursesContains(String course);

    List<Syncing> findAllByCompanyName(String companyName);

    List<Syncing> findAll();
}
