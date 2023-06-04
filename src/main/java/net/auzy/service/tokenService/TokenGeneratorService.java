package net.auzy.service.tokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class TokenGeneratorService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Value("${auth.issuer}")
    private String issuer;

    public String generateAccessToken(String userEmail) {
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .claim("sub", userEmail)
                .claim("exp", Instant.now().plus(Duration.ofMinutes(60)).getEpochSecond())
                .issuedAt(new Date().toInstant())
                .expiresAt(Instant.now().plus(Duration.ofMinutes(60)))
                .issuer(issuer)
                .subject(userEmail)
                .build();

        Jwt jwt  = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet));

        return jwt.getTokenValue();
    }
}




