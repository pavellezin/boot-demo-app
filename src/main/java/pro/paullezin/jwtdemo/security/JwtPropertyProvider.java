package pro.paullezin.jwtdemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:jwt.properties")
public class JwtPropertyProvider {

    private final String secret;
    private final Integer accessTokenExpirationTime;
    private final Integer refreshTokenExpirationTime;

    @Autowired
    public JwtPropertyProvider(@Value("${auth.jwt.secret}") String secret,
                               @Value("${access.token.expiration.time}") String access,
                               @Value("${refresh.token.expiration.time}") String refresh) {
        this.secret = secret;
        this.accessTokenExpirationTime = Integer.parseInt(access);
        this.refreshTokenExpirationTime = Integer.parseInt(refresh);
    }

    public String getSecret() {
        return secret;
    }

    public Integer getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public Integer getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }
}
