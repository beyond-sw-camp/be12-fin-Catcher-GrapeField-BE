//package com.example.grapefield.chat.controller;
//
//
//import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
//import com.example.grapefield.chat.model.request.ChatMessageReq;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/chat-test")
//@RequiredArgsConstructor
//public class ChatTestController {
//
//    private final KafkaTemplate<String, ChatMessageKafkaReq> kafkaTemplate;
//
//    @PostMapping("/send")
//    public ResponseEntity<String> sendTestMessage(@RequestBody ChatMessageReq req) {
//        ChatMessageKafkaReq event = new ChatMessageKafkaReq(
//                req.getRoomIdx(),
//                req.getSendUserIdx(),
//                req.getContent()
//        );
//
//        // roomIdx 기반 동적 토픽
//        String topic = "chat-" + req.getRoomIdx();
//        kafkaTemplate.send(topic, event);
//
//        return ResponseEntity.ok("Kafka 메시지 전송 완료");
//    }
//
//}
