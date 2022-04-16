package abc.jwt.config;


import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity //스프링 필터체인에 시큐리티 필터 추가 적용 (스프링 시큐리티는 이미 활성화중이지만)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsConfig corsConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {  //필터
        http.csrf().disable();//토큰 비활성화(테스트용)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        //해당 서버는 세션 사용 x -> 필수

        .and()
                .addFilter(corsConfig.corsFilter()) // cross origin 정책 무시 = 모든요청허용 -> 필수
                .formLogin().disable() // 폼형식으로 할 필 요없음.  -> 필수
                .httpBasic().disable() // http 로그인방식 이용 없음. -> 필수
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }
}