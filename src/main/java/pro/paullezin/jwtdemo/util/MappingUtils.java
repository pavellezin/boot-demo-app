package pro.paullezin.jwtdemo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pro.paullezin.jwtdemo.dto.UserDto;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class MappingUtils {
    private final PasswordEncoder passwordEncoder;

    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRoles());
        return userDto;
    }

    public User mapToNewUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getRoles() == null) {
            user.setRoles(EnumSet.of(Role.USER));
        } else {
            user.setRoles(userDto.getRoles());
        }
        return user;
    }

    public User mapToCurrentUser(UserDto userDto, User user) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getRoles() != null) {
            user.setRoles(userDto.getRoles());
        }
        return user;
    }
}
