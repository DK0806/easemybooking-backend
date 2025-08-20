package com.easemybooking.auth.security;

import com.easemybooking.auth.entity.UserEntity;
import com.easemybooking.auth.entity.RoleEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.SQLOutput;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        // Expect Base64 in jwt.secret (as in application.yml comments)
        String secretKey = "R40kJmeSk4U5iIDiuYVencVQyYtqMeAQ7Pq3Fmq+pvgy0fyFldOhwBNxT7Wjh5thSg4TkpR6tRLs9UboXjV4TA==";
        System.out.println("SECREEEETTTTT : "+props.secret());
        //this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.secret()));
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateAccessToken(UserEntity user) {
        var now = Instant.now();
        var exp = now.plusSeconds(props.accessTtlMinutes() * 60L);

        List<String> roles = user.getRoles()
                .stream()
                .map(RoleEntity::getName)
                .toList();
        System.out.print("SECRET KEY: "+key);
        System.out.print("ISSUER: "+props.issuer());

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(props.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", user.getEmail())
                .claim("name", user.getFullName())
                .claim("roles", roles) // store as JSON array
                .signWith(key) // HS256 inferred
                .compact();
    }

    public Claims parse(String token) {
        Jws<Claims> jws = Jwts.parser()
                .requireIssuer(props.issuer())
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return jws.getPayload();
    }
}
