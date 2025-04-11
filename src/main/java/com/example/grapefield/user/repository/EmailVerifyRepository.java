package com.example.grapefield.user.repository;

import com.example.grapefield.user.model.entity.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify,Long> {
  Optional<EmailVerify> findByUuid(String uuid);
}
