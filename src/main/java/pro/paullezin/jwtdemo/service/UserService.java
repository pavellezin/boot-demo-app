package pro.paullezin.jwtdemo.service;

import pro.paullezin.jwtdemo.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    //todo: never used, use or remove.
    void addRoleToUser(String username, String roleName);

    User getUser(String username);

    List<User> getUsers();
}