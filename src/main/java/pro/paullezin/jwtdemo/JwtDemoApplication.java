package pro.paullezin.jwtdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.service.UserService;

import java.util.EnumSet;

@SpringBootApplication
public class JwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.saveUser(new User("John Travolta", "john", "1234", EnumSet.of(Role.ADMIN, Role.USER)));
            userService.saveUser(new User("Will Smith", "will", "12345", EnumSet.of(Role.ADMIN, Role.USER)));
            userService.saveUser(new User("Bruce Willis", "bruce", "123456", EnumSet.of(Role.USER)));
        };
    }
}
