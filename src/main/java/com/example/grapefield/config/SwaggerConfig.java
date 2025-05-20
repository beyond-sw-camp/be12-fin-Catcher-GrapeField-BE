package com.example.grapefield.config;

import com.example.grapefield.config.filter.LoginFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.Optional;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components().addSecuritySchemes("BearerAuth",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("Grapefield API")
        .description("모두 함께 공연/전시 정보를 공유하고 후기를 교환할 수 있는 포도밭입니다.")
        .version("0.0.1");
  }

  @Bean
  public OpenApiCustomizer springSecurityLoginEndpointCustomizer(ApplicationContext applicationContext) {
    // Spring Security FilterChain 가져오기 전에 예외 처리 추가
    FilterChainProxy springSecurityFilterChain;
    try {
      springSecurityFilterChain = applicationContext.getBean("springSecurityFilterChain", FilterChainProxy.class);
    } catch (NoSuchBeanDefinitionException e) {
      // Spring Security FilterChain을 찾을 수 없을 경우 빈 커스터마이저 반환
      return openApi -> {};
    }

    return (openApi -> {
      for(SecurityFilterChain filterChain : springSecurityFilterChain.getFilterChains()) {
        // 스프링 시큐리티 특정 필터 가져오기(로그인)
        Optional<LoginFilter> filter = filterChain.getFilters().stream()
            .filter(LoginFilter.class::isInstance)
            .map(LoginFilter.class::cast)
            .findAny();

        if(filter.isPresent()) {
          // 문서 설정 객체
          Operation operation = new Operation();

          // 문서에서 요청설정
          Schema<?> schema = new ObjectSchema()
              .addProperty("email", new StringSchema().example("grapefield@grapefield.com"))
              .addProperty("password", new StringSchema().example("test1234"));
          RequestBody requestBody = new RequestBody().content(
              new Content().addMediaType("application/json", new MediaType().schema(schema))
          );
          operation.setRequestBody(requestBody);

          // 문서에서 응답 설정
          ApiResponses responses = new ApiResponses()
              .addApiResponse("200", new ApiResponse()
                  .description("로그인 성공")
                  .content(new Content().addMediaType("application/json",
                      new MediaType().example("""
                                {
                                "ATOKEN": "eyJhbGciOiJIUzI1NiIsInR5..."
                                }
                                """))))
              .addApiResponse("400", new ApiResponse()
                  .description("잘못된 요청입니다"));
          operation.setResponses(responses);

          // 직접 만든 필터의 문서를 swagger에 등록
          operation.addTagsItem("0. 로그인/로그아웃 설정");
          operation.summary("로그인");
          PathItem pathItem = new PathItem().post(operation);
          openApi.getPaths().addPathItem("/login", pathItem);
        }

        //로그아웃 필터 설정
        Optional<LogoutFilter> logoutFilter = filterChain.getFilters().stream()
            .filter(LogoutFilter.class::isInstance)
            .map(LogoutFilter.class::cast)
            .findAny();

        if(logoutFilter.isPresent()) {
          // 문서 설정 객체
          Operation operation = new Operation();
          // 문서에서 응답 설정
          ApiResponses response = new ApiResponses();
          response.addApiResponse(String.valueOf(HttpStatus.OK.value()),
              new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
          response.addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()),
              new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase()));
          operation.setResponses(response);

          // 직접 만든 필터의 문서를 swagger에 등록
          operation.addTagsItem("0. 로그인/로그아웃 설정");
          operation.summary("로그아웃");
          PathItem pathItem = new PathItem().post(operation);
          openApi.getPaths().addPathItem("/logout", pathItem);
        }
      }
    });
  }
}