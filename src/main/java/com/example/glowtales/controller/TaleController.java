package com.example.glowtales.controller;

import com.example.glowtales.dto.response.TaleResponseDto;
import com.example.glowtales.service.TaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Tale API", description = "API for managing tales")
public class TaleController {

//#001 전체 동화 상태창 불러오기
//
//#002 단어장 미리보기 불러오기
//#003 단어장 모두 불러오기
//
//#008 동화 만들기
//#009 동화의 학습 언어 선택하기
//#010 사진에서 키워드 추출하기
//
//#011 동화의 퀴즈와 정답 불러오기
//#012 동화 퀴즈의 답 제출하기



    private final TaleService tale_service;

    @Operation(summary = "#004 완료하지 않은 동화 미리보기 불러오기", description = "학습을 완료하지 않은 동화 중 최신 동화를 3개 불러오는 API입니다.")
    @GetMapping("/member/{memberId}/1")
    public ResponseEntity<List<TaleResponseDto>> getUnlearnedTalesTop3ByMemberId(@PathVariable Long memberId) {
        List<TaleResponseDto> posts = tale_service.getUnlearnedTaleTop3ByMemberId(memberId);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "#005 완료하지 않은 동화 모두 불러오기", description = "학습을 완료하지 않은 동화를 최신순으로 불러오는 API입니다.")
    @GetMapping("/member/{memberId}/2")
    public ResponseEntity<List<TaleResponseDto>> getUnlearnedTalesByMemberId(@PathVariable Long memberId) {
        List<TaleResponseDto> posts = tale_service.getUnlearnedTaleByMemberId(memberId);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "#006 최근 학습한 동화 미리보기 불러오기", description = "최근 학습한 동화 중 최신 동화를 3개 불러오는 API입니다.")
    @GetMapping("/member/{memberId}/3")
    public ResponseEntity<List<TaleResponseDto>> getStudiedTalesTop3ByMemberId(@PathVariable Long memberId) {
        List<TaleResponseDto> posts = tale_service.getStudiedTaleTop3ByMemberId(memberId);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "#007 최근 학습한 동화 모두 불러오기", description = "최근 학습한 동화를 최신순으로 불러오는 API입니다.")
    @GetMapping("/member/{memberId}/4")
    public ResponseEntity<List<TaleResponseDto>> getStudiedTalesByMemberId(@PathVariable Long memberId) {
        List<TaleResponseDto> posts = tale_service.getStudiedTaleByMemberId(memberId);
        return ResponseEntity.ok(posts);
    }


}
