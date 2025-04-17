package com.example.grapefield.events.repository;

import com.example.grapefield.events.model.entity.EventsImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsImgRepository extends JpaRepository<EventsImg, Long> {
  List<EventsImg> findByEventsIdxOrderByDisplayOrderAsc(Long eventsIdx);
}
