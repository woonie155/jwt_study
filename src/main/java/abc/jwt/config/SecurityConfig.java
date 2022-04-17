package abc.jwt.config;


import abc.jwt.config.jwt.JwtAuthenticationFilter;
import abc.jwt.config.jwt.JwtAuthorizationFilter;
import abc.jwt.filter.MyFilter1;
import abc.jwt.filter.MyFilter3;
import abc.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity //스프링 필터체인에 시큐리티 필터 추가 적용 (스프링 시큐리티는 이미 활성화중이지만)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {  //필터

        //http.addFilterBefore(new MyFilter1(), SecurityContextPersistenceFilter.class);
        //시큐리티 필터가 아닌것은, 시큐리티 필터들 중에(2번째 param지정) 그 이전 또는 그 이후에 삽입하라고 해야함.
        //굳이 시큐리티 구성에 걸필욘없음. -> FilterConfig 참조.
        //스프링 필터보단 시큐리티 필터가 우선순위임.
        // SecurityContextPersistenceFilter가 시큐리티 필터중 가장 최전방


        http
                .addFilter(corsConfig.corsFilter()) // cross origin 정책 무시 = 모든요청허용 -> 필수
                .csrf().disable()//토큰 비활성화(테스트용)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //해당 서버는 세션 사용 x -> 필수

        .and()
                .formLogin().disable() // 폼형식으로 로그인 할 필 요없음.  -> 필수
                .httpBasic().disable() // 해당 인증방식 사용x. -> 필수

                .addFilter(new JwtAuthenticationFilter(authenticationManager())) //해당 로그인필터를 등록
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
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