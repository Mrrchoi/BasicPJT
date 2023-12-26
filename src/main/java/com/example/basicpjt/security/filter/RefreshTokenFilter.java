package com.example.basicpjt.security.filter;

import com.example.basicpjt.domain.Token.RefreshToken;
import com.example.basicpjt.repository.RefreshTokenRepository;
import com.example.basicpjt.security.exception.RefreshTokenException;
import com.example.basicpjt.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.module.ResolutionException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    //  Token 갱신

    private final String refreshPath;

    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, RefreshTokenException {
        String path = request.getRequestURI();

        // refreshToken 경로로 온 경우만 처리
        if (!path.equals(refreshPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // AccessToken, RefreshToken Get
        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");

        Map<String, Object> accessClaims = null;

        // Token이 없거나 잘 못 된 경우 찾아내기 위함
        try {
            accessClaims = checkAccessToken(accessToken);
        } catch(RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }

        // redis에서 refreshToken 받아 오기
        String refreshToken = refreshTokenRepository.findById((String)accessClaims.get("mid"))
                .orElseThrow(() -> new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH)).getRefreshToken();

        Map<String, Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);

            // Refresh Token의 시간이 얼마 남지 않은 경우
            Integer exp = (Integer)refreshClaims.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

            Date current = new Date(System.currentTimeMillis());

            String mid = (String)refreshClaims.get("mid");

            // AccessToken 무조건 재발급
            String accessTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 1);

            // RefreshToken 만료 기간 계산
            long gapTime = expTime.getTime() - current.getTime();

            // 만료 기간이 3일보다 적게 남은 경우 Refresh Token 재생성
            if(gapTime < 1000 * 60 * 60 * 24 * 3) {
                refreshTokenRepository.save(new RefreshToken((String)refreshClaims.get("mid"), jwtUtil.generateToken(Map.of("mid", mid), 24 * 3)));
            }

            sendTokens(accessTokenValue, response);
        } catch(RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        // request JSON Data Get
        try (Reader reader = new InputStreamReader(request.getInputStream())){
            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private Map<String, Object> checkAccessToken(String accessToken) throws RefreshTokenException {
        // AccessToken Validation Check
        try {
            Map<String, Object> values = jwtUtil.validateToken(accessToken);

            return values;
        } catch(ExpiredJwtException expiredJwtException) {
            log.info("Access Token has Expired");
        } catch (Exception exception) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }

        return null;
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        // RefreshToken Validation Check
        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);

            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        } catch(MalformedJwtException malformedJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        } catch(Exception exception) {
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }

        return null;
    }

    private void sendTokens(String accessTokenValue, HttpServletResponse response) {
        // return Tokens
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}