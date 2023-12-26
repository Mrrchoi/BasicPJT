package com.example.basicpjt.domain.Token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@AllArgsConstructor
@RedisHash(value = "BlackList")
public class BlackList {

    private String mid;

    @Id
    private String accessToken;

    @TimeToLive
    private int expiration;
}