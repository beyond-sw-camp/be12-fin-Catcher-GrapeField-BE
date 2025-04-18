package com.example.grapefield.events;

import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
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


  public List<EventsListResp> searchEvents(String keyword) {
    Pageable top4 = PageRequest.of(0, 4);
    return eventsRepository.findEventsByKeyword(keyword, top4).getContent();
  }

  public Slice<EventsListResp> searchEvents(String keyword, Pageable pageable) {
    return eventsRepository.findEventsByKeyword(keyword, pageable);
  }

  public Slice<EventsListResp> searchEventsRefine(List<String> keywords, Pageable pageable) {
    return eventsRepository.findEventsByKeywordAnd(keywords, pageable);
  }

  public List<PostListResp> searchPosts(String keyword) {
    Pageable top10 = PageRequest.of(0, 10);
    return postRepository.findPostsByKeyword(keyword, top10).getContent();
  }
  public Slice<PostListResp> searchPosts(String keyword, Pageable pageable) {
    return postRepository.findPostsByKeyword(keyword, pageable);
  }

  public Slice<PostListResp> searchPostsRefine(List<String> keywords, Pageable pageable) {
    return postRepository.findPostsByKeywordAnd(keywords, pageable);
  }

  public List<ReviewListResp> searchReviews(String keyword) {
    Pageable top10 = PageRequest.of(0, 10);
    return reviewRepository.findReviewsByKeyword(keyword, top10).getContent();
  }

  public Slice<ReviewListResp> searchReviews(String keyword, Pageable pageable) {
    return reviewRepository.findReviewsByKeyword(keyword, pageable);
  }

  public Slice<ReviewListResp> searchReviewsRefine(List<String> keywords, Pageable pageable) {
    return reviewRepository.findReviewsByKeywordAnd(keywords, pageable);
  }
}
