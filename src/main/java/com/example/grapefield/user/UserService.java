package com.example.grapefield.user;

import com.example.grapefield.common.ImageService;
import com.example.grapefield.user.model.entity.AccountStatus;
import com.example.grapefield.user.model.entity.EmailVerify;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserSignupReq;
import com.example.grapefield.user.repository.EmailVerifyRepository;
import com.example.grapefield.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailVerifyRepository  emailVerifyRepository;
  private final JavaMailSender mailSender;
  private final ImageService imageService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(username);
    return user.orElse(null);
  }

  public void sendVerifyEmail(String uuid, String email) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("[가입인증] 포도밭 이메일 인증");
    message.setText(
        "포도밭 가입을 위해 아래 링크를 통해 인증을 진행해주세요.\n http://www.grapefield.kro.kr/email_verify/" + uuid
    );
    mailSender.send(message);
  }

  @Transactional
  public Boolean registerUser(UserSignupReq request) {
    //중복 이메일 확인
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return false;
    }
    MultipartFile file = request.getProfileImg();
    String filePath = null;
    //유저가 프로필 이미지를 업로드할때만 함수 호출
    if (file != null && !file.isEmpty()) {
      filePath = imageService.userProfileUpload(file);
    }
    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .profileImg(filePath) // null 허용
        .phone(request.getPhone())
        .build();
    userRepository.save(user);
    //이메일 인증용 uuid 생성하여 DB에 저장
    String uuid = UUID.randomUUID().toString();
    emailVerifyRepository.save(EmailVerify.builder().user(user).uuid(uuid).build());
    // 이메일 전송
    sendVerifyEmail(uuid, request.getEmail());
    return true;
  }

  public boolean verify(String uuid) {
    EmailVerify emailVerify = emailVerifyRepository.findByUuid(uuid).orElse(null);
    if(emailVerify == null) {
      return false;
    }
    User user = emailVerify.getUser();
    user.setStatus(AccountStatus.ACTIVE);
    userRepository.save(user);
    return true;
  }
}