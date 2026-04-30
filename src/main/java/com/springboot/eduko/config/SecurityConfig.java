package com.springboot.eduko.config;

import com.springboot.eduko.jwt.CustomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final CustomFilter customFilter;

    @Autowired
    public SecurityConfig(CustomFilter customFilter) {
        this.customFilter = customFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/signup").permitAll()
                                .requestMatchers("/resetPass").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .requestMatchers("/signupForTeacher").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                 .formLogin(AbstractHttpConfigurer::disable)
                 .httpBasic(AbstractHttpConfigurer::disable)
                 .cors(Customizer.withDefaults())
                 .logout(AbstractHttpConfigurer::disable)
                 .sessionManagement(AbstractHttpConfigurer::disable)
                 .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
         return http.build();

    }
}
