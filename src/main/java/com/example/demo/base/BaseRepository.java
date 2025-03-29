package com.example.demo.base;

import com.example.demo.base.model.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, Long> {
}