package co.com.bancolombia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
    "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
@ConfigurationPropertiesScan
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
