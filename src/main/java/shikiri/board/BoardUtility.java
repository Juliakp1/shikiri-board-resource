package shikiri.board;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import shikiri.board.exceptions.InvalidTokenException;

public class BoardUtility {

    public static String getUserIdFromToken(String token, SecretKey key) throws InvalidTokenException {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            try {
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                return claims.getSubject(); // Assuming the user ID is stored in the subject claim
            } catch (Exception e) {
                throw new InvalidTokenException("Failed to parse JWT token", e);
            }
        }
        throw new InvalidTokenException("Token is required but not provided");
    }
}

