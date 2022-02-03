package pro.paullezin.jwtdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.paullezin.jwtdemo.model.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}