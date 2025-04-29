package com.example.grapefield.user.customercenter;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customercenter")
@Tag(name="8. 고객센터", description = "공지사항, FAQ, QnA 등 고객 편의를 다루는 기능")
public class CustomerCenterController {


  //TODO: 공지사항, Qna, FaQ,
}
