package ie.atu.usersevice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class UserSeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserSeviceApplication.class, args);
    }

}
