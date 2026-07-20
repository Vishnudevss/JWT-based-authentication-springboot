package com.example.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final  UserDetailsService userDetailsService;
    private  final AuthStatusFilter authStatusFilter;

    public SecurityConfig(UserDetailsService userDetailsService, AuthStatusFilter authStatusFilter) {
        this.userDetailsService = userDetailsService;
        this.authStatusFilter = authStatusFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(customizer -> customizer.disable()).
                authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/api/v1/login",
                                "/api/v1/register",
                                "/api/v1/jwt/verify"
                        ).permitAll()
                        .anyRequest().authenticated()).
                httpBasic(Customizer.withDefaults()).
                sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logged out successfully");
                        }))
                .addFilterAfter(authStatusFilter,
                                SecurityContextHolderFilter.class)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config){
        return config.getAuthenticationManager();//gives the authenticationmanager that spring already have.
    }
    
}
