package org.gentle.puma.stockup.common.security.jwt.validator;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.gentle.puma.stockup.common.exception.CommonException;
import org.gentle.puma.stockup.common.exception.ErrorCode;

import java.security.Key;

public class JwtTokenValidator {

    public static void validate(String token, Key secretKey) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CommonException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CommonException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.MALFORMED_TOKEN);
        }
    }

}
