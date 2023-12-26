package com.example.basicpjt.config;

import com.example.basicpjt.repository.RefreshTokenRepository;
import com.example.basicpjt.security.CustomUserDetailsService;
import com.example.basicpjt.security.filter.APILoginFilter;
import com.example.basicpjt.security.filter.RefreshTokenFilter;
import com.example.basicpjt.security.filter.TokenCheckFilter;
import com.example.basicpjt.security.handler.APILoginFailureHandler;
import com.example.basicpjt.security.handler.APILoginSuccessHandler;
import com.example.basicpjt.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Log4j2
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final JWTUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("----------web configure----------");

        // 정적 리소스 요청 무시(스프링 시큐리티 적용 제외)
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

        // AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                        .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.authenticationManager(authenticationManager);

        // APILoginFilter
        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken");
        apiLoginFilter.setAuthenticationManager(authenticationManager);

        // APILoginSuccessHandler
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil, refreshTokenRepository);

        // SuccessHandler 설정
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);

        // APILoginFailureHandler
        APILoginFailureHandler failureHandler = new APILoginFailureHandler();

        // FailureHandler 설정
        apiLoginFilter.setAuthenticationFailureHandler(failureHandler);

        // APILoginFilter 위치 설정
        // 스프링 시큐리티에서 username과 password를 처리하는 UsernamePasswordAuthenticationFilter의 앞에서 동작
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        // api로 시작하는 모든 경로는 TokenCheckFilter 작동
        http.addFilterBefore(tokenCheckFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // refreshToken 호출
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil, refreshTokenRepository), TokenCheckFilter.class);

        //CSRF 토큰 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //세션 사용하지 않음
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CORS 설정
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private TokenCheckFilter tokenCheckFilter(CustomUserDetailsService customUserDetailsService, JWTUtil jwtUtil) {
        return new TokenCheckFilter(customUserDetailsService, jwtUtil);
    }
}
