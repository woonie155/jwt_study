package abc.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


//시큐리티 필터 시작전의 필터생성하고싶을때.
public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request; //다운캐스팅
        HttpServletResponse res = (HttpServletResponse) response;

        //해당 필터 역할
        //id, pw로 로그인 요청되고 로그인 가능하면 토큰을 만들어주고 응답.
        //다음 요청 시 마다 header에 Authorization의 value값에 토큰을 지정시키고
        //서버는 토큰이 '자신이 만든건지' 검증필요(RSA or HS256)
        System.out.println("필터1 실행시작.");
        if(req.getMethod().equals("POST")) {
            String headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth = " + headerAuth);

            if(headerAuth.equals("token123")){
                chain.doFilter(req, res); // 필터체인에 삽입.
            }
            else{
                PrintWriter out = res.getWriter();
                out.println("필터 인증 못거침");
            }
        }
        else{
            chain.doFilter(request, response);
        }
    }
}
