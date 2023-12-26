package com.example.basicpjt.repository;

import com.example.basicpjt.domain.member.Member;
import com.example.basicpjt.domain.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member member = Member.builder()
                    .mid("member" + i)
                    .mpw(passwordEncoder.encode("1234"))
                    .build();

            member.addRole(MemberRole.USER);

            if(i >= 6) member.addRole(MemberRole.ADMIN);

            memberRepository.save(member);
        });
    }
}
