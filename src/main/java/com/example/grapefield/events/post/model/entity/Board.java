package com.example.grapefield.events.post.model.entity;

import com.example.grapefield.events.model.entity.Events;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Board {
    @Id
    private Long idx;
    @MapsId
    @OneToOne
    @JoinColumn(name="idx")
    private Events events;
    private String title; //evnets.title가 동일, serivce에서 events의 title을 받아와서 저장

    @OneToMany(mappedBy = "board")
    private List<Post> postList;
}
