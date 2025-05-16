package com.example.grapefield.chat.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.result.Output;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextCortexResponse {
    public Data data;
    public String status;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        public List<Output> outputs;
        public int remaining_credits;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        public int index;
        public String text;
    }

}
