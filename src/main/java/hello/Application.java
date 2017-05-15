package hello;

import hello.persistence.LogRepository;
import hello.persistence.MongoJDBC;
import hello.storage.LogService;
import hello.storage.StorageProperties;
import hello.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableMongoRepositories
public class Application {

	@Autowired
	private LogRepository repo;

	public static void main(String[] args) {
		//MongoJDBC mongo = new MongoJDBC();
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
            //storageService.deleteAll();
            storageService.init();
			LogService.init(repo);
			repo.findAll();
		};
	}
}
