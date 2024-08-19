package com.example.glowtales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {
    @NotNull
    private Long languageId;
    @NotBlank
    private String learningLevel;
    @NotNull
    private Integer age;
}
