package co.za.turtletech.fuzaserver.persistance;

import co.za.turtletech.fuzaserver.model.DeviceContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceContentRepository extends MongoRepository<DeviceContent, Long> {
    @Query(value = "{'appRegistrationId' : ?0}", fields = "{'appRegistrationId' : 0}")
    List<DeviceContent> findDeviceContentByAppRegistrationId(String appRegistrationId);
}
