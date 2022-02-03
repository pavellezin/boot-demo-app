package pro.paullezin.jwtdemo.service;

import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleNme);

    User getUser(String username);

    List<User> getUsers();
}
