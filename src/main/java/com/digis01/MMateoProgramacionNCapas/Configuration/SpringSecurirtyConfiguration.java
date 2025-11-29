package com.digis01.MMateoProgramacionNCapas.Configuration;

import com.digis01.MMateoProgramacionNCapas.jwtutils.JwtAuthenticationEntryPoint;
import com.digis01.MMateoProgramacionNCapas.jwtutils.JwtFilter;
import com.digis01.MMateoProgramacionNCapas.jwtutils.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurirtyConfiguration {

    private JwtUserDetailsService userDetailsService;
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private JwtFilter filter;

    public SpringSecurirtyConfiguration(JwtUserDetailsService userDetailsJPAService, JwtAuthenticationEntryPoint authenticationEntryPoint, JwtFilter filter) {
        this.userDetailsService = userDetailsJPAService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.filter = filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(c -> c.disable()).authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/**")
                .hasAnyRole("ADMIN", "USER", "STUDENT", "TEACHER")
                .anyRequest().authenticated()
        ).exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)).
                sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
//                .authenticationProvider(authenticationProvider())
                .logout(logout -> logout
                .permitAll()
                ).userDetailsService(userDetailsService);

        return httpSecurity.build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }

}
