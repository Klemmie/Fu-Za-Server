package co.za.turtletech.fuzaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FuZaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuZaServerApplication.class, args);
	}

}
