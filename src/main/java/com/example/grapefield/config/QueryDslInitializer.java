package com.example.grapefield.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class QueryDslInitializer {

    @PostConstruct
    public void init() {
        // 명시적으로 QEvents를 먼저 초기화
        try {
            //의존성 체인의 루트를 먼저 초기화
            Class.forName("com.example.grapefield.events.model.entity.QEvents");
            Thread.sleep(100); // 약간의 지연을 두어 초기화 완료 시간 확보
            Class.forName("com.example.grapefield.notification.model.entity.QEventsInterest");
            Thread.sleep(100);
            Class.forName("com.example.grapefield.notification.model.entity.QScheduleNotification");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}