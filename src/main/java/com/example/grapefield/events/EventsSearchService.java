package com.example.grapefield.events;

import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.model.response.PostSearchListResp;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.model.response.ReviewSearchList;
import com.example.grapefield.events.review.repository.ReviewRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventsSearchService {
  private final EventsRepository eventsRepository;
  private final PostRepository postRepository;
  private final ReviewRepository reviewRepository;

  public List<EventsListResp> searchEvents(String keyword, User user) {
    Pageable top4 = PageRequest.of(0, 6);
    return eventsRepository.findEventsByKeyword(keyword, top4, user).getContent();
  }

  public Page<EventsListResp> searchEvents(String keyword, Pageable pageable, User user) {
    return eventsRepository.findEventsByKeyword(keyword, pageable, user);
  }

  public Page<EventsListResp> searchEventsRefine(List<String> keywords, Pageable pageable, User user) {
    return eventsRepository.findEventsByKeywordAnd(keywords, pageable, user);
  }

  public List<PostSearchListResp> searchPosts(String keyword, User user) {
    Pageable top10 = PageRequest.of(0, 10);
    return postRepository.findPostsByKeyword(keyword, top10, user).getContent();
  }

  public Page<PostSearchListResp> searchPosts(String keyword, Pageable pageable, User user) {
    return postRepository.findPostsByKeyword(keyword, pageable, user);
  }

  public Page<PostSearchListResp> searchPostsRefine(List<String> keywords, Pageable pageable, User user) {
    return postRepository.findPostsByKeywordAnd(keywords, pageable, user);
  }

  public List<ReviewSearchList> searchReviews(String keyword, User user) {
    Pageable top10 = PageRequest.of(0, 10);
    return reviewRepository.findReviewsByKeyword(keyword, top10, user).getContent();
  }

  public Page<ReviewSearchList> searchReviews(String keyword, Pageable pageable, User user) {
    return reviewRepository.findReviewsByKeyword(keyword, pageable, user);
  }

  public Page<ReviewSearchList> searchReviewsRefine(List<String> keywords, Pageable pageable, User user) {
    return reviewRepository.findReviewsByKeywordAnd(keywords, pageable, user);
  }
}
