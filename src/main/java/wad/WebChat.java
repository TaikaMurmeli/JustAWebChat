package wad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import wad.profiles.DevProfile;
import wad.profiles.ProdProfile;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@Import({DevProfile.class, ProdProfile.class})
public class WebChat {

    public static void main(String[] args) {
        SpringApplication.run(WebChat.class, args);
    }
}
