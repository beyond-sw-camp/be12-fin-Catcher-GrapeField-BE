package com.example.grapefield.user.repository;

import com.example.grapefield.user.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
  Optional<User> findByEmail(String email);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE User u SET u.username = :username, u.profileImg = :profileImg, u.phone = :phone WHERE u.idx = :idx")
  int updateUserFields(@Param("idx") Long idx,
                       @Param("username") String username,
                       @Param("profileImg") String profileImg,
                       @Param("phone") String phone);
}
