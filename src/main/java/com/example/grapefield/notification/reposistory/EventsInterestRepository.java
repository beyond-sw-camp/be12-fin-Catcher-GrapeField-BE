package com.example.grapefield.notification.reposistory;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventsInterestRepository extends JpaRepository<EventsInterest, Long>, EventsInterestCustomRepository {
  // User와 Events로 조회
  Optional<EventsInterest> findByUserAndEvents(User user, Events events);
}
