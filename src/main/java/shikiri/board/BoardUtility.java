package shikiri.board;

import javax.crypto.SecretKey;
import org.springframework.http.HttpHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import shikiri.board.exceptions.InvalidTokenException;

import java.util.List;

public class BoardUtility {
    public static String getUserBySecretKey(HttpHeaders headers, SecretKey key) {
        String userId = null;
        if (headers.containsKey("Authorization")) {
            List<String> authorizationHeader = headers.get("Authorization");
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                String authorization = authorizationHeader.get(0);
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    String jwt = authorization.substring(7); // Remove "Bearer " prefix
                    try {
                        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                        userId = claims.getSubject(); // Or use the appropriate claim for userId
                    } catch (Exception e) {
                        throw new InvalidTokenException("Failed to parse JWT token", e);
                    }
                }
            }
        }
        return userId;
    }
}

