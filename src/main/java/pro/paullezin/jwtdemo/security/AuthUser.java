package pro.paullezin.jwtdemo.security;

import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import pro.paullezin.jwtdemo.model.User;

@Getter
@ToString(of = "user")
public class AuthUser extends org.springframework.security.core.userdetails.User {

    private final User user;

    public AuthUser (@NonNull User user){
        super(user.getUsername(), user.getPassword(), user.getRoles());
        this.user = user;
    }

}