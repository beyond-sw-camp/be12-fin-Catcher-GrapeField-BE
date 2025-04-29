package com.example.grapefield.events.participant;

import com.example.grapefield.events.repository.EventsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParticipantService {
  private final EventsRepository eventsRepository;

  public Map<String, Object> getParticipantDetail(Long idx) {
    return eventsRepository.getParticipantDetail(idx);
  }
}
