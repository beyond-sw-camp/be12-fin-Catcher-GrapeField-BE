package com.example.grapefield.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "events", createIndex = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDocument {
    // Elasticsearch 문서 ID
    @Id
    private String id;  // Elasticsearch의 _id (문자열)

    // 실제 데이터 필드들
    @Field(type = FieldType.Long)
    private Long idx;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String title;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date endDate;

    @Field(type = FieldType.Text)
    private String posterImgUrl;

    @Field(type = FieldType.Text)
    private String venue;

    @Field(type = FieldType.Boolean)
    private Boolean isVisible;

    // 현재 있는 필드들은 유지
    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String postTitle;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String postContent;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String review;
}