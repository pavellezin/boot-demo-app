package pro.paullezin.jwtdemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:jwt.properties")
public class JwtPropertyProvider {

    private String secret;

    @Autowired
    public JwtPropertyProvider(@Value("${auth.jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}
