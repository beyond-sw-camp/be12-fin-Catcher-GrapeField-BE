package com.example.grapefield.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeywordExtractionService {

    /**
     * 메시지들에서 키워드 추출
     */
    public String extractKeywords(List<String> messages) {
        if (messages.isEmpty()) {
            return "활발한 채팅";
        }

        // 모든 메시지 합치기
        String combinedText = String.join(" ", messages);

        // 단어 빈도 계산
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = combinedText.toLowerCase()
                .replaceAll("[^가-힣a-z0-9\\s]", " ")
                .split("\\s+");

        for (String word : words) {
            if (word.length() > 1 && !isStopWord(word)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // 빈도 높은 단어 3개 추출
        String keywords = wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        return keywords.isEmpty() ? "활발한 채팅" : keywords + " 관련 대화";
    }

    /**
     * 불용어 체크 (간단한 버전)
     */
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
                "이", "그", "저", "것", "수", "있", "없", "하", "되", "되다", "있다", "없다",
                "the", "is", "at", "which", "on", "and", "or", "to", "a", "an",
                "ㅋㅋ", "ㅎㅎ", "ㅋㅋㅋ", "ㅎㅎㅎ", "ㅠㅠ", "ㅜㅜ"
        );
        return stopWords.contains(word);
    }

    /**
     * 메트릭 정보와 키워드를 결합한 설명 생성
     */
    public String createDescription(String keywords, double spikeRatio) {
        return String.format("%s (활동급증 %.1fx)",
                keywords.isEmpty() ? "활발한 채팅" : keywords,
                spikeRatio);
    }
}