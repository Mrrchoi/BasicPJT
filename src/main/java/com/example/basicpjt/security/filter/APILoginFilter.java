package com.example.basicpjt.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Log4j2
public class APILoginFilter extends AbstractAuthenticationProcessingFilter {

    //인증 단계를 처리 및 성공시 Access Token, Refresh Token 전송
    //AbstractAuthenticationProcessingFilter: 로그인 처리 담당 -> 로그인 처리 경로 설정 및 AuthenticationManager 객체 설정 필수
    public APILoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // Json Data 사용
        Map<String, String> jsonData = parseRequestJSON(request);

        // 전달 받은 정보로 로그인 처리
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(jsonData.get("mid"), jsonData.get("mpw"));

        //AuthenticationProvider 호출
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        // request의 Json Data 처리
        try(Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);
        } catch(Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
