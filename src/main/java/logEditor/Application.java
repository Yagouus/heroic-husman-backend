package logEditor;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties()

public class Application {



	public static void main(String[] args) {
		//MongoJDBC mongo = new MongoJDBC();
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return (args) -> {

		};
	}
}
