package hello.persistence;

import hello.dataTypes.Log;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository <Log, String>{

    public Log findByName(String name);

}
