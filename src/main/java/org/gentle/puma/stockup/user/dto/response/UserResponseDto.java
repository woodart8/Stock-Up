package org.gentle.puma.stockup.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    @JsonProperty("nickname")
    String nickname;

    String email;

}
