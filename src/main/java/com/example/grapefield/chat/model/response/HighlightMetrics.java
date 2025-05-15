package com.example.grapefield.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HighlightMetrics {
    private final double currentMessageRate;
    private final double averageMessageRate;
    private final long activeUserCount;
    private final double spikeRatio;
}
