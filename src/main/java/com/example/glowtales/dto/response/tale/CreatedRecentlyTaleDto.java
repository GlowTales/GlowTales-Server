package com.example.glowtales.dto.response.tale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreatedRecentlyTaleDto {
    private List<TaleDto> tales;
}
