package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final CustomAuthExceptionHandler customAuthExceptionHandler;
    private final CorsConfig config;

    public SecurityConfiguration(CustomAuthExceptionHandler customAuthExceptionHandler, CorsConfig config) {
        this.customAuthExceptionHandler = customAuthExceptionHandler;
        this.config = config;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable()) // Ở mô hình stateless không dùng token "csrf"
                .cors(c -> c.configurationSource(config.corsConfigurationSource())) // Sử dụng cấu hình
                .authorizeHttpRequests(
                        authz ->
                        // prettier-ignore
                        authz
                                .requestMatchers("/", "/api/users", "api/v1/auth/login", "api/v1/register",
                                        "api/v1/users", "api/v1/auth/refresh")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthExceptionHandler))
                // .exceptionHandling(
                // exceptions -> exceptions
                // .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
                // .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403

                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Step 1: Create a converter to extract authorities from JWT claims
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Step 2: Configure the converter
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // Remove default "SCOPE_" prefix
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permissions"); // Look for "permissions" claim in JWT

        // Step 3: Create the main JWT authentication converter
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

}
