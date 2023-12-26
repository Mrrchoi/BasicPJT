package com.example.basicpjt.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberJoinDTO {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9-_]{4,15}$")
    private String mid;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$")
    private String mpw;
}
