package pro.paullezin.jwtdemo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.service.UserService;
import pro.paullezin.jwtdemo.util.ValidationUtil;

import java.net.URI;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/users/register")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        ValidationUtil.checkNew(user);
        user.setRoles(EnumSet.of(Role.USER));
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users")
                .build().toUri();
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

}