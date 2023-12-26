package com.example.basicpjt.controller;

import com.example.basicpjt.dto.member.MemberJoinDTO;
import com.example.basicpjt.dto.member.MemberRequestDTO;
import com.example.basicpjt.service.member.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/generateToken").forward(request, response);
    }

    @PostMapping(value = "/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        memberService.logout((String)request.getAttribute("token"), (String)request.getAttribute("mid"), (Integer)request.getAttribute("exp"));

    }

    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void join(@Valid @RequestBody MemberJoinDTO memberJoinDTO, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        try {
            memberService.join(memberJoinDTO);
        } catch(MemberService.MidExistException e) {
            bindingResult.addError(new FieldError("", "mid", "", true, new String[]{"Same Id"}, null, ""));
            throw new BindException(bindingResult);
        }
    }
}
