package pro.paullezin.jwtdemo.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "username_uniqe", columnNames = {"username"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"password"})
public class User extends BaseEntity implements Serializable {

    private String name;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_unique")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
}