package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.response.HighlightDetectionResp;
import com.example.grapefield.chat.model.response.TextCortexResponse;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class TextCortexSummarizer {
    private final EmojiReplaceService emojiReplaceService;
    private final KeywordExtractionService keywordExtractionService;
    private static final String API_URL = "https://api.textcortex.com/v1/texts/summarizations";
    @Value("${textcortex.key}")
    private String API_KEY; // = "gAAAAABoKGvz49-wgOE5XWuoy8Q2lPq0P7UCz50e92XbpwxyUF_ChtodpcnG6Zk18YcQ-D7lBnASli5AucBBK0oYy0arzqyeRykGv6LQVE5V9TkXEW5fP_-1ylDIH1TfnZHHUo4p16tffIU7_Poi5p2X-eofgTncwUou-Hlb6kmsKdl71HQSYwg=";

    public String intoOneLine(List<String> messageList) {
        String inputText = "";
        for (String message : messageList) {
            inputText = inputText + "\'" + message + "\', ";
        }
        inputText = emojiReplaceService.replaceEmojis(inputText);
        return inputText;
    }

    public String summarize(String inputText, HighlightDetectionResp detectionResp) throws Exception {
//        String requestBody = String.format("{\"text\": \"%s\", \"source_lang\": \"ko\", \"target_lang\": \"ko\", \"max_tokens\": 10, \"formality\": \"less\"}",
//                inputText.replace("\"", "\\\"")); // JSON escape
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(API_URL))
//                .header("Authorization", "Bearer "+API_KEY)
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+API_KEY)
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"formality\": \"less\",\n  \"max_tokens\": 10,\n  \"mode\": \"default\",\n  \"model\": \"gemini-2-0-flash\",\n  \"n\": 1,\n  \"source_lang\": \"ko\",\n  \"target_lang\": \"ko\",\n  \"temperature\": null,\n  \"text\": \""+inputText+"\"\n}"))
                .build();
        HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String respBodyJsonString = httpResponse.body();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(
                JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(),
                false
        );
        TextCortexResponse responseObj = objectMapper.readValue(respBodyJsonString, TextCortexResponse.class);

        String outputKeyword;

        if (Objects.equals(responseObj.status, "success")){
            outputKeyword = responseObj.data.outputs.get(0).text;
        } else {
            log.info("⚠️[키워드 저장 중 API 오류] 요청에 실패 status=\"failure\"");
            outputKeyword = keywordExtractionService.createDescription(keywordExtractionService.extractKeywords(detectionResp.getRecentMessages()), detectionResp.getMetrics().getSpikeRatio());
        }
        return outputKeyword;
    }
}
