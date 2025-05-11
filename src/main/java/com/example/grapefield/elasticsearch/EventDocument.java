package com.example.grapefield.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDocument {
    @Id
    private String idx;

    @Field(type = FieldType.Text)  // analyzer 설정 제거
    private String title;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Text)  // analyzer 설정 제거
    private String postTitle;

    @Field(type = FieldType.Text)  // analyzer 설정 제거
    private String postContent;

    @Field(type = FieldType.Text)  // analyzer 설정 제거
    private String review;
}