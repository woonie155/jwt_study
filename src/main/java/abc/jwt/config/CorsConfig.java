package abc.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //서버가 응답할때, json을 자바스크립트에 처리가능여부를 설정
        config.addAllowedOrigin("*"); // 허용 ip 선택
        config.addAllowedHeader("*"); // 허용 header 선택
        config.addAllowedMethod("*"); // 허용 메소드 방식 선택

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
