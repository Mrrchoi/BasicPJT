package com.example.basicpjt.service.member;

import com.example.basicpjt.dto.member.MemberJoinDTO;

public interface MemberService {

    class MidExistException extends Exception {

    }

    void logout(String token, String mid, Integer exp);

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
}
