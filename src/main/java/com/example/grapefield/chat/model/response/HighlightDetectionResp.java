package com.example.grapefield.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class HighlightDetectionResp {
    private final HighlightMetrics metrics;
    private final List<String> recentMessages;
}
