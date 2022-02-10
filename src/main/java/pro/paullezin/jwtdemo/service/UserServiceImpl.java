package pro.paullezin.jwtdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.paullezin.jwtdemo.config.WebSecurityConfig;
import pro.paullezin.jwtdemo.error.IllegalRequestDataException;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.repo.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    public User saveUser(User user) {
        log.info("Save new user [{}]", user.getUsername());
        user.setPassword(WebSecurityConfig.PASSWORD_ENCODER.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Add new role [{}] to user [{}]", roleName, username);
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalRequestDataException("User '" + username + "' not found"));
        Role role = Role.valueOf(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("Get user [{}]", username);
        return userRepo.findByUsername(username).orElseThrow(() -> new IllegalRequestDataException("User '" + username + "' not found"));
    }

    @Override
    public List<User> getUsers() {
        log.info("Get all users");
        return userRepo.findAll();
    }
}