package com.easemybooking.places.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    //private final JwtProperties props;
    private final String issuer = "easemybooking-auth";
    private final int accessTtlMinutes=30;
    private final SecretKey key;
    public JwtService() {
        //this.props = props;
        // Expect Base64 in jwt.secret (as in application.yml comments)
        String secretKey = "R40kJmeSk4U5iIDiuYVencVQyYtqMeAQ7Pq3Fmq+pvgy0fyFldOhwBNxT7Wjh5thSg4TkpR6tRLs9UboXjV4TA==";
        //System.out.println("SECREEEETTTTT : "+props.secret());
        //this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.secret()));
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    //private final SecretKey = Keys.hmacShaKeyFor("R40kJmeSk4U5iIDiuYVencVQyYtqMeAQ7Pq3Fmq+pvgy0fyFldOhwBNxT7Wjh5thSg4TkpR6tRLs9UboXjV4TA==".getBytes(StandardCharsets.UTF_8));

//    public JwtService(JwtProperties props) {
//        this.props = props;
//        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
//    }

//    public String generateAccessToken(UUID subject, Map<String, Object> claims) {
//        Instant now = Instant.now();
//        Instant exp = now.plusSeconds(props.accessTtlMinutes() * 60L);
//
//        return Jwts.builder()
//                .subject(subject.toString())
//                .issuer(props.issuer())
//                .issuedAt(Date.from(now))
//                .expiration(Date.from(exp))
//                .claims(claims)
//                .signWith(key, Jwts.SIG.HS256)  // jjwt 0.12.x API
//                .compact();
//    }

    public Claims parse(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
        return jws.getPayload();
    }
}
