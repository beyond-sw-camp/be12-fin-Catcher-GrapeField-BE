package com.example.demo.events.post.model.entity;

import com.example.demo.events.model.entity.Events;
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

    @OneToMany(mappedBy = "board")
    private List<Post> postList;
}
