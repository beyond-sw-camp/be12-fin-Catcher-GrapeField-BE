package com.example.grapefield.events.repository;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsRepository extends JpaRepository<Events, Long>, EventsCustomRepository {
  List<Events> findByTitle(String title);
  List<Events> findByCategory(EventCategory category);

  Page<EventsListResp> findEventsByIdx(Long idx, Pageable pageable);
//  @Query("SELECT new com.example.grapefield.events.model.response.EventsListResp(...) " +
//          "FROM Events e WHERE ... ")
//  Page<EventsListResp> findEventsByKeyword(String keyword, Pageable pageable, User user);
}