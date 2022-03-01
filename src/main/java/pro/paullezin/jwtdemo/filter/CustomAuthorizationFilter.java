package pro.paullezin.jwtdemo.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.paullezin.jwtdemo.error.ResponseError;
import pro.paullezin.jwtdemo.security.JwtPropertyProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
//todo use @Component instead of manual instantiating of bean
//TODO provide more specific info in class name, what is 'custom'? use 'Jwt' instead or smth like that
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final JwtPropertyProvider jwtPropertyProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filter) throws ServletException, IOException {
        //todo instead of this you may override shouldNotFilter() method from parent
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            filter.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    Algorithm alg = Algorithm.HMAC256(jwtPropertyProvider.getSecret().getBytes());
                    JWTVerifier jwtVerifier = JWT.require(alg).build();
                    DecodedJWT decodedJWT = jwtVerifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filter.doFilter(request, response);
                } catch (Exception e) {
                    //todo use global Exception handler instead (inject bean here and invoke manually)
                    ResponseError.build(response, e);
                }
            } else {
                filter.doFilter(request, response);
            }
        }
    }
}