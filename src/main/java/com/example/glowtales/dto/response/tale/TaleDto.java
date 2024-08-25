package com.example.glowtales.dto.response.tale;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaleDto {
    private Long taleId;
    private String title;
    private String createdAt;
}
