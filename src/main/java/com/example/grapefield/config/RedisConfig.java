package com.example.grapefield.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
    //refresh token 관리용 redisTemplate
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 직렬화 설정 추가
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());

    return template;
  }

  /**
    JSON 직렬화를 사용하는 RedisTemplate<String, Object> 추가
    - 채팅방 참여자(Set<String>), 하트 수(Integer), 복합 DTO 등을 캐싱할 때 사용
   */
  /**
   *  // 캐싱이 필요한 서비스에서는
  // @Qualifier("jsonRedisTemplate")으로 주입받아 사용할 수 있다.
  // (아래 참고)
    @Service
    public class ChatService {
        private final RedisTemplate<String,Object> redisJson;

        public ChatService(@Qualifier("jsonRedisTemplate") RedisTemplate<String,Object> redisJson) {
            this.redisJson = redisJson;
        }

        // 채팅방 참여자(Set<String>) 캐싱 예
        public void addParticipant(Long roomId, String userId) {
            redisJson.opsForSet()
                     .add("chat:"+roomId+":participants", userId);
        }
    }

  * */
  @Bean // key: String, value: Object (JSON)
  public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 키와 해시 키는 문자열 직렬화
    StringRedisSerializer stringSer = new StringRedisSerializer();
    template.setKeySerializer(stringSer);
    template.setHashKeySerializer(stringSer);

    // 값과 해시 값은 JSON 직렬화
    GenericJackson2JsonRedisSerializer jsonSer = new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(jsonSer);
    template.setHashValueSerializer(jsonSer);

    // 설정 반영
    template.afterPropertiesSet();
    return template;
  }

}
