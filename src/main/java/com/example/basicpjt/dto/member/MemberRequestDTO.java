package com.example.basicpjt.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRequestDTO {

    private String mid;

    private String mpw;
}

