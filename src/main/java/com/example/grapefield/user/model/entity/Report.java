package com.example.grapefield.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"type", "item"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suspect_id", nullable = false)
    private User suspect; // 신고당한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // 신고자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportTargetType type;
    @Column(nullable = false)
    private Long item; // 해당 type의 실제 대상(PK), 예: post_idx, comment_idx 등
    @Column(nullable = false)
    private int reportCnt = 1;
    @Column(nullable = false)
    private Boolean isAccepted = false;
    @Column(length = 255)
    private String reason;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    // 신고 횟수 증가
    public void increaseCnt() {
        this.reportCnt += 1;
    }
    // 관리자 승인 처리
    public void accept() {
        this.isAccepted = true;
    }
}
