package com.example.basicpjt.service.member;

import com.example.basicpjt.domain.Token.BlackList;
import com.example.basicpjt.domain.member.Member;
import com.example.basicpjt.domain.member.MemberRole;
import com.example.basicpjt.dto.member.MemberJoinDTO;
import com.example.basicpjt.repository.BlackListRepository;
import com.example.basicpjt.repository.MemberRepository;
import com.example.basicpjt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final ModelMapper modelMapper;

    private final MemberRepository memberRepository;

    private final BlackListRepository blackListRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void logout(String token, String mid, Integer exp) {
        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

        Date current = new Date(System.currentTimeMillis());

        int gapTime = (int)(expTime.getTime() - current.getTime()) / 1000;

        blackListRepository.save(new BlackList(mid, token, gapTime));

        refreshTokenRepository.deleteById(mid);
    }

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();

        boolean exist = memberRepository.existsById(mid);

        if(exist) {
            throw new MidExistException();
        }

        Member member = modelMapper.map(memberJoinDTO, Member.class);
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        member.addRole(MemberRole.USER);

        log.info("====================");
        log.info(member);
        log.info(member.getRoleSet());

        memberRepository.save(member);
    }
}