package pro.paullezin.jwtdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.repo.RoleRepo;
import pro.paullezin.jwtdemo.repo.UserRepo;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Override
    public User saveUser(User user) {
        log.info("Save new user [{}]", user.getUsername());
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Save new role [{}]", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleNme) {
        log.info("Add new role [{}] to user [{}]", roleNme, username);
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleNme);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("Get user [{}]", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        log.info("Get all users");
        return userRepo.findAll();
    }
}
