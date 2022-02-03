package pro.paullezin.jwtdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.paullezin.jwtdemo.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
