package csieReserve.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("https://csiereserve.store").description("Production Server")); // 배포 서버
        servers.add(new Server().url("http://localhost:8080").description("Local Server"));          // 로컬 서버

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("JWT", securityScheme())) // SecurityScheme 추가
                .info(info())
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("JWT")); // 모든 API에 JWT 인증을 요구
    }

    private Info info() {
        return new Info()
                .title("가톨릭대학교 컴퓨터정보공학부 회의실 예약 Swagger")
                .description("컴퓨터정보공학부 회의실 예약 서비스 통신 API")
                .version("1.0.0");
    }

    // JWT 인증 방식 설정 (Authorization 헤더)
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // APIKEY 타입으로 설정
                .in(SecurityScheme.In.HEADER) // HTTP 헤더에 JWT를 포함시킴
                .name("Authorization") // Authorization 헤더 사용
                .description("JWT 인증 토큰을 'Bearer' 형식으로 입력"); // JWT 사용 방법 설명
    }
}
