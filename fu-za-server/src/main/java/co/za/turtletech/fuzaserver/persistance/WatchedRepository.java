package co.za.turtletech.fuzaserver.persistance;

import co.za.turtletech.fuzaserver.model.Watched;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document
public interface WatchedRepository extends MongoRepository<Watched, Long> {
    List<Watched> findByCellNumber(String cellNumber);
}
