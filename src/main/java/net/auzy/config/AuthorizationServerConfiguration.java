package net.auzy.config;

import net.auzy.dao.application.ApplicationClientRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * https://github.com/spring-projects/spring-authorization-server/blob/main/samples/demo-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java
 */

@Configuration
public class AuthorizationServerConfiguration {

//    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    @Autowired
    private ApplicationClientRepository applicationClientRepository;

    @Value("${auth.issuer}")
    private String issuer;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        return applicationClientRepository;
    }

    /*
     * http://localhost:8000/oauth2/authorize?response_type=code&client_id=be-hate-me-2&redirect_uri=http://localhost:8001/auth&scope=Profile-Write%20Profile-Read%20openid
     */

    @Bean
    @Order(2)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http
                .sessionManagement(configurer ->
                        configurer
                                .sessionConcurrency(
                                        sessionConcurrency ->
                                                sessionConcurrency
                                                        .maximumSessions(1)
                                                        .expiredUrl("/login?expired")
                                )
                                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
        );

        http
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        return http
                .formLogin(
                        loginConfigure ->
                                loginConfigure
                                        .loginPage("/auth-login")
                                        .permitAll()
                )
                .logout(loginConfigure ->
                        loginConfigure
                                .logoutUrl("/auth-logout")
                                .permitAll()
                )
                .build();
    }

    @Bean
    @Order(1) // add a new filter chain specially for resource server endpoints
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/secure/**")
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain configureSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/public/**")
                                .permitAll()
                                .requestMatchers( "/contents/**", "/css/**" )
                                .permitAll()
                                .requestMatchers("/.well-known/*")
                                .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(
                        loginConfigure ->
                                loginConfigure
                                        .loginPage("/auth-login")
                                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("issuer")
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                Authentication principal = context.getPrincipal();

                Set<String> authorities = principal
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                context.getClaims().claim("roles", authorities);
            }
        };
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) throws JOSEException {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(JWKSet jwkSet) throws NoSuchAlgorithmException {
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JWKSet getJwkSet() throws NoSuchAlgorithmException {
        return new JWKSet(generateRsa());
    }

    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

}