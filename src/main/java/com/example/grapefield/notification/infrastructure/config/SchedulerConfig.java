package com.example.grapefield.notification.infrastructure.config;

import com.example.grapefield.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {
  private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);


  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10); // 동시에 실행될 수 있는 작업 수
    scheduler.setThreadNamePrefix("notification-scheduler-");
    scheduler.setErrorHandler(throwable -> {
      // 에러 처리 로직
      log.error("Scheduled task error", throwable);
    });
    scheduler.setAwaitTerminationSeconds(60);
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    return scheduler;
  }
}