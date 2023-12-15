package gr.aueb.cf.scoresapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/","/home","/players", "/teams", "/results", "/players/select",
                        "/players/selected/**", "/teams/select", "/teams/selected/**",
                        "/players/stats/**","/players/rankings", "/teams/select","/teams/selected/**",
                        "/results/select", "/results/selected/**").permitAll() // Public pages
                .antMatchers("/players/update/**", "/players/delete/**", "/teams/update/**",
                        "/teams/delete/**","/results/update/**",
                        "/results/delete/**").hasRole("ADMIN") // Admin pages
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // Customized login page
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/home") // Redirect to home page after logout
                .permitAll();

    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin1 = User.builder()
                .username("admin1")
                .password(passwordEncoder().encode("password1"))
                .roles("ADMIN")
                .build();

        UserDetails admin2 = User.builder()
                .username("admin2")
                .password(passwordEncoder().encode("password2"))
                .roles("ADMIN")
                .build();

        UserDetails admin3 = User.builder()
                .username("admin3")
                .password(passwordEncoder().encode("password3"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin1, admin2, admin3);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
