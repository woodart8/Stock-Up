package org.gentle.puma.stockup.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

    @JsonProperty("nickname")
    private String nickname;

    private String email;

    private String password;

    @JsonProperty("sign_up_path")
    private String signUpPath;

}
