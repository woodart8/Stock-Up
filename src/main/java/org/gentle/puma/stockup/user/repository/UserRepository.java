package org.gentle.puma.stockup.user.repository;

import org.gentle.puma.stockup.user.entity.UserEntity;
import org.gentle.puma.stockup.user.enums.SignUpPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndSignUpPath(String email, SignUpPath signUpPath);

}
