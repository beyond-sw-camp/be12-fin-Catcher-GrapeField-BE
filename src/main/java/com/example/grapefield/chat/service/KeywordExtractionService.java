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

        log.info("키워드 추출 시작: {} 개 메시지", messages.size());

        // 1. 키워드 추출 시도
        String extractedKeywords = tryExtractKeywords(messages);

        // 2. 키워드 추출 성공 시 반환
        if (!extractedKeywords.equals("활발한 채팅")) {
            log.info("키워드 추출 성공: {}", extractedKeywords);
            return extractedKeywords;
        }

        // 3. 키워드 추출 실패 시 마지막 메시지 사용
        String lastMessage = messages.get(messages.size() - 1);

        // 메시지가 너무 길면 자르기
        if (lastMessage.length() > 50) {
            String trimmed = lastMessage.substring(0, 47) + "...";
            log.info("키워드 추출 실패 → 마지막 메시지 사용 (잘림): {}", trimmed);
            return trimmed;
        }

        log.info("키워드 추출 실패 → 마지막 메시지 사용: {}", lastMessage);
        return lastMessage;
    }

    /**
     * 키워드 추출 시도 (개선된 조건)
     */
    private String tryExtractKeywords(List<String> messages) {
        // 모든 메시지 합치기
        String combinedText = String.join(" ", messages);

        // 단어 빈도 계산
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = combinedText.toLowerCase()
                .replaceAll("[^가-힣a-z0-9\\s]", " ")
                .split("\\s+");

        for (String word : words) {
            // 조건 완화: 1글자 이상, 관대한 불용어 처리
            if (word.length() >= 1 && !isStopWord(word)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // 빈도 높은 단어 3개 추출
        List<String> topWords = wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 의미있는 키워드가 있는지 확인
        if (topWords.isEmpty() || topWords.size() < 2) {
            return "활발한 채팅";
        }

        String keywords = String.join(", ", topWords);
        return keywords + " 관련 대화";
    }

    /**
     * 불용어 체크 (최소한으로 제한)
     */
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
                // 의미없는 감정표현만 제외
                "ㅋㅋ", "ㅎㅎ", "ㅋㅋㅋ", "ㅎㅎㅎ", "ㅠㅠ", "ㅜㅜ", "ㅇㅇ", "ㅏ", "ㅜ", "ㅗ"
        );
        return stopWords.contains(word);
    }

    /**
     * 설명 생성 (내용만 저장)
     */
    public String createDescription(String content, double spikeRatio) {
        // 내용만 저장 (급증 정보 제거)
        return content;
    }
}