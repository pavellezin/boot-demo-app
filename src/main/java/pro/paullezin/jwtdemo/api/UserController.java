package pro.paullezin.jwtdemo.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pro.paullezin.jwtdemo.dto.UserDto;
import pro.paullezin.jwtdemo.error.IllegalRequestDataException;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.security.JwtPropertyProvider;
import pro.paullezin.jwtdemo.service.UserService;
import pro.paullezin.jwtdemo.util.MappingUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtPropertyProvider jwtPropertyProvider;
    private final MappingUtils mappingUtils;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers().stream().map(mappingUtils::mapToUserDto).collect(Collectors.toList()));
    }

    @GetMapping("/users/find/by-name")
    public ResponseEntity<UserDto> getUser(@RequestParam(value = "username") String username) {
        return ResponseEntity.ok().body(mappingUtils.mapToUserDto(userService.getUser(username)));
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        User user = mappingUtils.mapToNewUser(userDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users")
                .build().toUri();
        return ResponseEntity.created(uri).body(mappingUtils.mapToUserDto(userService.saveUser(user)));
    }

    @PutMapping(value = "/users/update", consumes = APPLICATION_JSON_VALUE)
    public void updateUser(@Validated @RequestBody UserDto userDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        User oldUser;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Algorithm alg = Algorithm.HMAC256(jwtPropertyProvider.getSecret().getBytes());
            JWTVerifier jwtVerifier = JWT.require(alg).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String username = decodedJWT.getSubject();
            oldUser = userService.getUser(username);
            User user = mappingUtils.mapToCurrentUser(userDto, oldUser);
            userService.saveUser(user);
        } else {
            throw new IllegalRequestDataException("Refresh token is missing.");
        }
    }
}