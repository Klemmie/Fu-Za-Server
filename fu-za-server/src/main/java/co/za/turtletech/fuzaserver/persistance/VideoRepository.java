package co.za.turtletech.fuzaserver.persistance;

import co.za.turtletech.fuzaserver.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video, Long> {
    @Query(value = "{'course' : ?0}", fields = "{'course' : 0}")
    List<Video> findVideoByCourse(String course);

    Video findVideoByName(String name);

    Video findByGuid(String guid);
}
