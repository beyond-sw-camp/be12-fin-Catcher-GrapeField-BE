package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
