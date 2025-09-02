package org.gentle.puma.stockup.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.gentle.puma.stockup.common.security.jwt.JwtToken;

@Getter
@Builder
public class TokenResponseDto {
    private JwtToken token;
}
