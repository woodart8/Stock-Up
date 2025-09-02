package org.gentle.puma.stockup.user.service;

import org.gentle.puma.stockup.common.exception.CommonException;
import org.gentle.puma.stockup.common.exception.ErrorCode;
import org.gentle.puma.stockup.user.dto.response.UserResponseDto;
import org.gentle.puma.stockup.user.entity.UserEntity;
import org.gentle.puma.stockup.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto loadUserById(long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return UserResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

}