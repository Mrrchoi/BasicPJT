package com.example.basicpjt.security.handler;

import com.example.basicpjt.domain.Token.RefreshToken;
import com.example.basicpjt.repository.RefreshTokenRepository;
import com.example.basicpjt.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인 성공시 토큰 발행
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> claim = Map.of("mid", authentication.getName());

        String accessToken = jwtUtil.generateToken(claim, 1);

        String refreshToken = jwtUtil.generateToken(claim, 24 * 7);

        Gson gson = new Gson();

        // accesstoken만 반환하고 refreshtoken은 redis에서 관리

        Map<String, String> keyMap = Map.of("accessToken", accessToken);

        refreshTokenRepository.save(new RefreshToken(authentication.getName(), refreshToken));

        String jsonStr = gson.toJson(keyMap);

        response.getWriter().println(jsonStr);
    }
}
