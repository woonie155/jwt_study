package abc.jwt.config;

import abc.jwt.filter.MyFilter1;
import abc.jwt.filter.MyFilter2;
import abc.jwt.filter.MyFilter3;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFilter2> filter2(){
        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
        bean.addUrlPatterns("/*"); //모든 요청에대해 필터 적용
        bean.setOrder(0); //낮은 번호지정일수록, 가장 먼저 실행.
        return bean;
    }
    @Bean
    public FilterRegistrationBean<MyFilter3> filter3(){
        FilterRegistrationBean<MyFilter3> bean = new FilterRegistrationBean<>(new MyFilter3());
        bean.addUrlPatterns("/*"); //모든 요청에대해 필터 적용
        bean.setOrder(1); //0 다음 1 실행.
        return bean;
    }
}
