package logEditor.persistence;

import logEditor.dataTypes.LogFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository <LogFile, String>{

    public LogFile findByName(String name);

}
