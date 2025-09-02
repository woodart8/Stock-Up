package org.gentle.puma.stockup.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.gentle.puma.stockup.common.security.jwt.JwtToken;

@Getter
@Builder
public class LoginResponseDto {

    @JsonProperty("user_id")
    private Long userId;
    private JwtToken token;

}
