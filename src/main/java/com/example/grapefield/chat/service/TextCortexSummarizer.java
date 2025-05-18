package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.response.TextCortexResponse;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
    private static final String API_URL = "https://api.textcortex.com/v1/texts/summarizations";
    // 환경변수 처리하기
    private static final String API_KEY = "${textcortex.api-key}";


    public String intoOneLine(List<String> messageList) {
        String inputText = "";
        for (String message : messageList) {
            inputText = inputText + "\'" + message + "\', ";
        }
        inputText = emojiReplaceService.replaceEmojis(inputText);
        return inputText;
    }

    public String summarize(String inputText) throws Exception {
//        String requestBody = String.format("{\"text\": \"%s\", \"source_lang\": \"ko\", \"target_lang\": \"ko\", \"max_tokens\": 10, \"formality\": \"less\"}",
//                inputText.replace("\"", "\\\"")); // JSON escape
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(API_URL))
//                .header("Authorization", "Bearer "+API_KEY)
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.textcortex.com/v1/texts/summarizations"))
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
            if (responseObj != null && responseObj.data != null && responseObj.data.outputs != null && !responseObj.data.outputs.isEmpty()) {
                outputKeyword = responseObj.data.outputs.get(0).text;
            } else if (responseObj.data.remaining_credits <= 0) {
                log.info("⚠️ [키워드 저장 중...] API 사용량 초과 크레딧 충전 필요");
                log.info("🗒️응답내용 status:{}, data.outputs: {}, data.outputs.remaining_credits: {}", responseObj.status, responseObj.data.outputs, responseObj.data.remaining_credits);

                outputKeyword = "요약 불가 status=\"success\"";
            } else {
                log.info("⚠️[키워드 저장 중 API 오류] 올바르지 않은 응답 status=\"success\"");
                log.info("🗒️응답내용 status:{}, data.outputs: {}, data.outputs.remaining_credits: {}", responseObj.status, responseObj.data.outputs, responseObj.data.remaining_credits);
                outputKeyword = "응답오류 status=\"success\"";
            }
        } else {
            log.info("⚠️[키워드 저장 중 API 오류] 요청에 실패 status=\"failure\"");
            log.info("🗒️ responseObj.toString(): {}",responseObj);
            log.info("🗒️응답내용 status:{}, data.outputs: {}, data.outputs.remaining_credits: {}", responseObj.status, responseObj.data.outputs, responseObj.data.remaining_credits);
            outputKeyword = "응답 오류 status=\"failure\"";
        }

        return outputKeyword;




    }
}
