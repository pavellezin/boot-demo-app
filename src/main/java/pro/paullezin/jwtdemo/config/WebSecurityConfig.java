package pro.paullezin.jwtdemo.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pro.paullezin.jwtdemo.filter.JwtAuthenticationFilter;
import pro.paullezin.jwtdemo.filter.JwtAuthorizationFilter;
import pro.paullezin.jwtdemo.model.Role;
import pro.paullezin.jwtdemo.model.User;
import pro.paullezin.jwtdemo.repo.UserRepo;
import pro.paullezin.jwtdemo.security.AuthUser;
import pro.paullezin.jwtdemo.security.JwtPropertyProvider;

import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepo userRepo;
    private final JwtPropertyProvider jwtPropertyProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public UserDetailsService userDetailsService() {
        return username -> {
            log.debug("Authenticating '{}'", username);
            Optional<User> optionalUser = userRepo.findByUsername(username);
            return new AuthUser(optionalUser.orElseThrow(
                    () -> new UsernameNotFoundException("User '" + username + "' not found")
            ));
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManagerBean(), jwtPropertyProvider);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**", "/api/users/register/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/api/users/update/**").hasRole(Role.USER.name());
        http.authorizeRequests().antMatchers(GET, "/api/users/**").hasRole(Role.ADMIN.name());
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(jwtAuthenticationFilter);
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}