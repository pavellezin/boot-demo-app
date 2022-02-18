package pro.paullezin.jwtdemo.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pro.paullezin.jwtdemo.error.ResponseError;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.security.JwtPropertyProvider;
import pro.paullezin.jwtdemo.service.UserService;
import pro.paullezin.jwtdemo.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pro.paullezin.jwtdemo.util.ValidationUtil.assertConsistent;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtPropertyProvider jwtPropertyProvider;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/users/find/by-name")
    public ResponseEntity<User> getUser(@RequestParam(value = "username") String username) {
        return ResponseEntity.ok().body(userService.getUser(username));
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        ValidationUtil.checkNew(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users")
                .build().toUri();
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PutMapping(value = "/users/update", consumes = APPLICATION_JSON_VALUE)
    public void updateUser(@Validated @RequestBody User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        User oldUser = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm alg = Algorithm.HMAC256(jwtPropertyProvider.getSecret().getBytes());
                JWTVerifier jwtVerifier = JWT.require(alg).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                String username = decodedJWT.getSubject();
                oldUser = userService.getUser(username);
            } catch (Exception e) {
                ResponseError.build(response, e);
            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
        assertConsistent(user, oldUser.getId());
        user.setRoles(oldUser.getRoles());
        if (user.getUsername() == null) {
            user.setUsername(oldUser.getUsername());
        }
        if (user.getPassword() == null) {
            user.setPassword(oldUser.getPassword());
        }
        userService.saveUser(user);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm alg = Algorithm.HMAC256(jwtPropertyProvider.getSecret().getBytes());
                JWTVerifier jwtVerifier = JWT.require(alg).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + jwtPropertyProvider.getAccessTokenExpirationTime() * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toList()))
                        .sign(alg);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                ResponseError.build(response, e);
            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }
}