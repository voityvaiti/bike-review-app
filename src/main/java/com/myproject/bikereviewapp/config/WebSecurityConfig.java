package com.myproject.bikereviewapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String STUFF = "STUFF";


    private final UserDetailsService userDetailsService;


//    @Bean
//    public UserDetailsManager authenticateUsers() {
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM usr WHERE username = ?");
//        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT username, role FROM usr WHERE username = ?");
//        return jdbcUserDetailsManager;
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/reviews/**")).hasAnyAuthority(ADMIN, STUFF)
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/**")).hasAuthority(ADMIN)

                        .requestMatchers(antMatcher("/**/users/**/admin/**")).hasAuthority(ADMIN)
                        .requestMatchers(antMatcher("/**/admin/**")).hasAnyAuthority(ADMIN, STUFF)


                        .requestMatchers(antMatcher(HttpMethod.POST, "/reviews/**")).authenticated()
                        .requestMatchers(antMatcher("/users/profile/**")).authenticated()
                
                        .anyRequest().permitAll())

                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login-error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(encoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
