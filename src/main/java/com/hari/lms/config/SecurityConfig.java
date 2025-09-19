package com.hari.lms.config;

import com.hari.lms.security.UserDetailsServiceImpl;
import com.hari.lms.security.jwt.AuthEntryPointJwt;
import com.hari.lms.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security configuration for the LMS application.
 * Configures JWT authentication, authorization, and security filters.
 * 
 * @author Hari Parthu
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private AuthEntryPointJwt unauthorizedHandler;

        /**
         * JWT authentication filter bean.
         */
        @Bean
        public AuthTokenFilter authenticationJwtTokenFilter() {
                return new AuthTokenFilter();
        }

        /**
         * DAO authentication provider bean.
         */
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        /**
         * Authentication manager bean.
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        /**
         * Password encoder bean.
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Security filter chain configuration.
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.cors(cors -> cors.disable())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")) // Disable
                                                                                                               // CSRF
                                                                                                               // for
                                                                                                               // API
                                                                                                               // endpoints
                                                                                                               // only
                                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                                .authorizeHttpRequests(authz -> authz
                                                // Public endpoints - API
                                                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                                                .requestMatchers(new AntPathRequestMatcher("/api/public/**"))
                                                .permitAll()

                                                // Public web pages
                                                .requestMatchers(new AntPathRequestMatcher("/"),
                                                                new AntPathRequestMatcher("/home"),
                                                                new AntPathRequestMatcher("/login"),
                                                                new AntPathRequestMatcher("/signup"),
                                                                new AntPathRequestMatcher("/css/**"),
                                                                new AntPathRequestMatcher("/js/**"),
                                                                new AntPathRequestMatcher("/images/**"))
                                                .permitAll()
                                                .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()

                                                // Swagger/OpenAPI endpoints
                                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"),
                                                                new AntPathRequestMatcher("/swagger-ui.html"),
                                                                new AntPathRequestMatcher("/api-docs/**"),
                                                                new AntPathRequestMatcher("/v3/api-docs/**"))
                                                .permitAll()

                                                // Health check endpoints
                                                .requestMatchers(new AntPathRequestMatcher("/actuator/health"),
                                                                new AntPathRequestMatcher("/actuator/info"))
                                                .permitAll()

                                                // H2 Console (development only)
                                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                                                .permitAll()

                                                // Role-based access for web pages
                                                .requestMatchers(new AntPathRequestMatcher("/admin/**"))
                                                .hasRole("ADMIN")
                                                .requestMatchers(new AntPathRequestMatcher("/instructor"),
                                                                new AntPathRequestMatcher("/instructor/**"))
                                                .hasAnyRole("ADMIN", "INSTRUCTOR")
                                                .requestMatchers(new AntPathRequestMatcher("/student"),
                                                                new AntPathRequestMatcher("/student/**"))
                                                .hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")

                                                // Role-based access for API endpoints
                                                .requestMatchers(new AntPathRequestMatcher("/api/admin/**"))
                                                .hasRole("ADMIN")
                                                .requestMatchers(new AntPathRequestMatcher("/api/instructor/**"))
                                                .hasAnyRole("ADMIN", "INSTRUCTOR")
                                                .requestMatchers(new AntPathRequestMatcher("/api/student/**"))
                                                .hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")

                                                // Course endpoints (allow browsing, protect enrollment)
                                                .requestMatchers(new AntPathRequestMatcher("/courses")).permitAll()
                                                .requestMatchers(new AntPathRequestMatcher("/courses/**")).permitAll()
                                                .requestMatchers(new AntPathRequestMatcher("/api/courses/**"))
                                                .authenticated()

                                                // Default: require authentication
                                                .anyRequest().authenticated())

                                // Form-based login for web interface
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                // Logout configuration
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID", "XSRF-TOKEN", "remember-me")
                                                .permitAll())

                                // Session management for web
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false))

                                // Exception handling for API endpoints
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(unauthorizedHandler))

                                // Authentication provider
                                .authenticationProvider(authenticationProvider());

                // Add JWT filter for API endpoints only
                http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

                // Configure headers for H2 console
                http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

                return http.build();
        }
}