package com.example.grapefield.events.model.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SeatPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String seatType;
    private BigDecimal price;
    private String description;

    @ManyToOne
    @JoinColumn(name = "events_idx")
    private Events events;

}