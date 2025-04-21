package com.example.grapefield.events.review.repository;

import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.model.response.ReviewSearchList;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ReviewCustomRepository {
  Page<ReviewListResp> findReviewList(Long idx, Pageable pageable, Long rating, User user);
  Page<ReviewSearchList> findReviewsByKeyword(String keyword, Pageable top10, User user);
  Page<ReviewSearchList> findReviewsByKeywordAnd(List<String> keywords, Pageable pageable, User user);
}
