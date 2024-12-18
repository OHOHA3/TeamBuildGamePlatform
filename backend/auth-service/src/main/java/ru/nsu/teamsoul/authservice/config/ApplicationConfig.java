package ru.nsu.teamsoul.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.nsu.teamsoul.authservice.repository.UsersRepository;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UsersRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(applicationPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean(name = "applicationPasswordEncoder")
    public PasswordEncoder applicationPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}