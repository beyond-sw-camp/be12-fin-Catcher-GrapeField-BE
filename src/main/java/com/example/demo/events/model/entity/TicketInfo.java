package com.example.demo.events.model.entity;

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

    @ManyToOne
    @JoinColumn(name = "idx")
    private Events events;

    @Enumerated(EnumType.STRING)
    private TicketVendor ticketVendor;

    private String ticketLink;
    private Boolean isPresale;
    private LocalDateTime saleStart;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
