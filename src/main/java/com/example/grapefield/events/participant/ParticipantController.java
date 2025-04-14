package com.example.grapefield.events.participant;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.events.model.response.EventsDetailResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/participant")
@Tag(name="3.1. 출연진, 기업 정보", description = "공연/전시에 참가하는 출연진 및 기업 정보를 등록하고 목록 및 상세 정보를 확인")
public class ParticipantController {
  private final ParticipantService participantService;
  //TODO : 배우 등록
  
  //TODO : 기업 등록

  //TODO : 배우 목록 불러오기(공연/전시 등록 페이지용)

  //TODO : 기업 목록 불러오기(공연/전시 등록 페이지용)

  @Operation(summary = "배우, 기업 목록 조회", description = "공연/전시 상세 페이지에서 해당 이벤트에 출연하는 출연진 정보와 제작, 후원 기업 정보 목록 반환")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{idx}")
  public ResponseEntity<Map<String, Object>> getParticipantDetail(@PathVariable Long idx
  ) {
    Map<String, Object> response = participantService.getParticipantDetail(idx);
    return ResponseEntity.ok(response);
  }

}
