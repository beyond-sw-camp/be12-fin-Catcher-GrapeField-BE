package com.example.grapefield.notification.controller;

import com.example.grapefield.notification.service.EventsInterestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/interest")
@Tag(name="즐겨찾기 기능", description = "공연/전시를 즐겨찾기")
public class EventInterestController {
  private EventsInterestService eventsInterestService;
}
