package com.example.grapefield.events.review.repository;

import com.example.grapefield.events.review.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long>, ReviewCustomRepository{
}
