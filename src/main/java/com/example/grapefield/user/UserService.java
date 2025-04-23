package com.example.grapefield.user;

import com.example.grapefield.common.ImageService;
import com.example.grapefield.events.post.model.response.UserCommentListResp;
import com.example.grapefield.events.post.model.response.UserPostListResp;
import com.example.grapefield.events.post.model.response.UserReviewListResp;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.events.review.repository.ReviewRepository;
import com.example.grapefield.user.model.entity.AccountStatus;
import com.example.grapefield.user.model.entity.EmailVerify;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.request.UserInfoDetailReq;
import com.example.grapefield.user.model.request.UserSignupReq;
import com.example.grapefield.user.model.response.UserInfoDetailResp;
import com.example.grapefield.user.repository.EmailVerifyRepository;
import com.example.grapefield.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerifyRepository emailVerifyRepository;
    private final JavaMailSender mailSender;
    private final ImageService imageService;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ReviewRepository reviewRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(username);
        User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
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
        if (emailVerify == null) {
            return false;
        }
        User user = emailVerify.getUser();
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
        return true;
    }

    public UserInfoDetailResp getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        return new UserInfoDetailResp(
                user.getUsername(),
                user.getEmail(),
                "********", // 비밀번호는 마스킹 처리
                user.getPhone(),
                user.getProfileImg(),
                user.getCreatedAt()
        );
    }

    public boolean verifyPassword(String rawPassword, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        String a = passwordEncoder.encode(rawPassword);
        System.out.println("새로운 비번: \n" + a);
        // BCryptPasswordEncoder로 비교
        return encoder.matches(rawPassword, user.get().getPassword());
    }

    @Transactional
    public boolean updateUser(UserInfoDetailReq request, User user) {
        // 기존 사용자 엔티티 조회 (영속 상태)
        User managedUser = userRepository.findById(user.getIdx())
                .orElseThrow(() -> new RuntimeException("사용자 찾기 실패"));

        // 변경 사항만 업데이트
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            managedUser.setUsername(request.getUsername());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            managedUser.setPhone(request.getPhoneNumber());
        }

        // 이미지 처리
        if (request.getProfileImg() != null && !request.getProfileImg().isEmpty()) {
            String imageUrl = imageService.userProfileUpload(request.getProfileImg());
            managedUser.setProfileImg(imageUrl);
        }

        // 변경 감지(dirty checking)가 자동으로 업데이트 수행
        return true;
    }

    public Page<UserPostListResp> getUserPosts(Long userIdx, Pageable pageable) {
        return postRepository.postsFindByUserIdx(userIdx, pageable);
    }


    public Page<UserCommentListResp> getUserComments(Long userIdx, Pageable pageable) {
        return postRepository.commentsFindByUserIdx(userIdx, pageable);
    }

    public Page<UserReviewListResp> getUserReviews(Long userIdx, Pageable pageable) {
        return reviewRepository.reviewsFindByUserIdx(userIdx, pageable);
    }
}