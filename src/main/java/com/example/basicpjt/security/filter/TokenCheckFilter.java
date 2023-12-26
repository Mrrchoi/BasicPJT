package com.example.basicpjt.security.filter;

import com.example.basicpjt.repository.BlackListRepository;
import com.example.basicpjt.security.CustomUserDetailsService;
import com.example.basicpjt.security.exception.AccessTokenException;
import com.example.basicpjt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.expression.ExpressionInvocationTargetException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    // 하나의 요청에 대해서 한번씩 JWT Token 검사

    private final CustomUserDetailsService customUserDetailsService;

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // api로 시작하는 경우 && GET 요청이 아닌 경우에만 Token 검사
        if(!path.startsWith("/api/") || path.contains("/login") || path.contains("/join") || method.equalsIgnoreCase("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token 유효성 검사
        try {
            Map<String, Object> payload = validateAccessToken(request);

            String mid = (String)payload.get("mid");

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(mid);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if(path.contains("/logout")) {
                request.setAttribute("mid", mid);
                request.setAttribute("token", request.getHeader("Authorization").substring(7));
                request.setAttribute("exp", payload.get("exp"));
                SecurityContextHolder.getContext().setAuthentication(null);
            }

            filterChain.doFilter(request, response);
        } catch(AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }
    }

    // Access Token 검증
    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization");

        if(headerStr == null || headerStr.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (!tokenType.equalsIgnoreCase("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);

            return values;
        } catch(MalformedJwtException malformedJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch(SignatureException signatureException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }
}
