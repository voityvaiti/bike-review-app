package com.myproject.bikereviewapp.security;

import com.myproject.bikereviewapp.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String ADMIN = Role.ADMIN.name();
    private static final String STUFF = Role.STUFF.name();



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/reviews/**")).hasAnyAuthority(ADMIN, STUFF)
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/**")).hasAuthority(ADMIN)

                        .requestMatchers(antMatcher("/**/users/**/admin/**")).hasAuthority(ADMIN)
                        .requestMatchers(antMatcher("/**/admin/**")).hasAnyAuthority(ADMIN, STUFF)


                        .requestMatchers(antMatcher("/reviews/**")).authenticated()
                        .requestMatchers(antMatcher("/users/profile/**")).authenticated()
                
                        .anyRequest().permitAll())

                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login-error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")

                ).csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
