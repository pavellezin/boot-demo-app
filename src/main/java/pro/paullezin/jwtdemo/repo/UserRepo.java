package pro.paullezin.jwtdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.paullezin.jwtdemo.model.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}