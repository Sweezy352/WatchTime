package kg.sweezy.watchtime.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kg.sweezy.watchtime.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtHandler {
    @Value("${jwt.secret.key}")
    private String key;
    @Value("${jwt.expiration.time}")
    private Long timeExpiration;
    private SecretKey secretKey;

    public SecretKey getSecretKey() {
        if(secretKey == null) {
            byte[]byteSecretKey = Base64.getDecoder().decode(key);
            System.out.println(byteSecretKey);
            secretKey = Keys.hmacShaKeyFor(byteSecretKey);
        }
        return secretKey;
    }

    public String jwtGenerateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + timeExpiration);
        Map<String, Object> claims = new HashMap<>();
        UserEntity user = (UserEntity) userDetails;
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    public String jwtParseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        }catch (ExpiredJwtException ex){
            log.info("------>>>>>" + ex.getClass() + " " + ex.getMessage());
        }catch (UnsupportedJwtException ex){
            log.info("------>>>>>" + ex.getClass() + " " + ex.getMessage());
        }catch (IllegalArgumentException ex){
            log.info("------>>>>>" + ex.getClass() + " " + ex.getMessage());
        }catch (MalformedJwtException ex){
            log.info("------>>>>>" + ex.getClass() + " " + ex.getMessage());
        }catch (SignatureException ex){
            log.info("------>>>>>" + ex.getClass() + " " + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
