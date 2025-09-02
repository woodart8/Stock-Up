package org.gentle.puma.stockup.user.service;

import org.gentle.puma.stockup.user.dto.response.UserResponseDto;

public interface UserService {

    UserResponseDto loadUserById(long id);

}
