package pro.paullezin.jwtdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pro.paullezin.jwtdemo.model.Role;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(exclude = {"password"})
public class UserDto implements Serializable {
    private String name;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Set<Role> roles;
}
