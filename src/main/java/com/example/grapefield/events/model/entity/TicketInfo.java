package com.example.grapefield.events.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TicketInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String ticketLink; // 예매 링크
    private Boolean isPresale; //선예매 여부
    private LocalDateTime saleStart; //예매 시작일
    private LocalDateTime saleEnd; //예매 종료일
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "events_idx")
    private Events events;

    @Enumerated(EnumType.STRING)
    private TicketVendor ticketVendor;

}
