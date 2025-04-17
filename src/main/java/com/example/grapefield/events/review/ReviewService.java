package com.example.grapefield.events.review;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.review.model.entity.Review;
import com.example.grapefield.events.review.model.request.ReviewRegisterReq;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.repository.ReviewRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final EventsRepository eventsRepository;

  public Long registerReview(ReviewRegisterReq request, User user) {
    Events events = eventsRepository.findById(request.getEventIdx()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 행사입니다."));
    //한줄평 저장
    Review review = Review.builder()
        .user(user)
        .events(events)
        .content(request.getContent())
        .rating(request.getRating())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    Review saveReview = reviewRepository.save(review);
    return saveReview.getIdx();
  }

  public Page<ReviewListResp> getReviewList(Long idx, Pageable pageable, Long rating, User user) {
    return reviewRepository.findReviewList(idx, pageable, rating, user);
    //TODO : 특정 점수만 가져오는 기능 추가 필요
  }
}
