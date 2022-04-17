package abc.jwt.config.jwt;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import abc.jwt.config.auth.PrincipalDetails;
import abc.jwt.dto.LoginRequestDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;


/**
 *  시큐리티의 UsernamePasswordAuthenticationFilter는 로그인 post요청 시 동작.
 *  하지만 formLogin().disable()설정으로 해당 필터 동작 안하므로 따로 적용시켜야함.
 *  = 세션없는 stateful + 권한을 완벽히 구현하기 위해서는 권한 관리 로직을 따로 구현
 *  시큐리티가 세션값이용해 권한확인 자동으로하므로, 권한로직직접짤필욘없음. (직접짜면 principalDetails 세션안넣어도댐)
 *
 *  Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
 *  Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override //post 로그인 요청 시(/login) , 실행되는 함수
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        System.out.println("유저네임필터 동작");

       //아이디, 패스워드 파싱
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //받은 정보로 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword());

        //해당토큰으로 로그인 시도(실제 로그인되는-인증된)(해당 시점에 PrincipalDetailsService의 loadUserByUsername()호출.)
        Authentication authentication =
                authenticationManager.authenticate(authenticationToken); //로그인안되면 401에러
//        PrincipalDetails pd = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println("Authentication : "+pd.getUser().getUsername());
        //로그인 가능경우, authentication 객체는 세션영역에 저장됨. -> 따라서 getPrincipal()이 가능

        return authentication; //필터체인으로 리턴해줌.
    }


    /**
     * attemptAuthentication 실행 후, 인증된 사용자일 시 successfulAuthentication 호출
     * 해당 jwt 토큰을, 로그인 요청한 웹쪽에다 응답하기 위한.
     * (클라이언트는 JWT 토큰을 가지고 있다가 재요청시마다 JWT 토큰을 가지고 요청)
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        PrincipalDetails pd_profile = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(pd_profile.getUsername()) //토큰의 이름
                .withExpiresAt(new Date(System.currentTimeMillis()+ (1000*60*10))) //만료시간 10분
                .withClaim("id", pd_profile.getUser().getId()) // 비공개 클레임 - 선택
                .withClaim("username", pd_profile.getUser().getUsername()) // 비공개 클레임 - 선택
                .sign(Algorithm.HMAC512("jeawoon")); //HMAC HS256에 쓰일 개인키

        response.addHeader("Authorization", "Bearer "+jwtToken); //헤더 담아 응답
    }

    //추가적으로 + 요청마다 JWT토큰이 유효한지 판단할 필터 설계계 필요
}

