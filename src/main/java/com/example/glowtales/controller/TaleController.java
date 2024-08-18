package com.example.glowtales.controller;

import com.example.glowtales.dto.response.HomeInfoResponseDto;
import com.example.glowtales.dto.response.TaleResponseDto;
import com.example.glowtales.dto.response.WordResponseDto;
import com.example.glowtales.service.TaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tale")
@Tag(name = "Tale API", description = "API for managing tales")
public class TaleController {

//
//#008 동화 만들기
//#009 동화의 학습 언어 선택하기
//#010 사진에서 키워드 추출하기
//
//#011 동화의 퀴즈와 정답 불러오기
//#012 동화 퀴즈의 답 제출하기


    private final TaleService tale_service;

    @Operation(summary = "#001 전체 동화 상태창 조회", description = "홈 화면에 나타나는 상태를 불러오는 API입니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<HomeInfoResponseDto> getHomeInfoByMemberId(@PathVariable Long memberId) {
        HomeInfoResponseDto infos = tale_service.getHomeInfoByMemberId(memberId);
        return ResponseEntity.ok(infos);
    }


//    @Operation(summary = "#004 완료하지 않은 동화 미리보기 불러오기", description = "학습을 완료하지 않은 동화 중 최신 동화를 3개 불러오는 API입니다.")
//    @GetMapping("/unlearned/top3/{memberId}")
//    public ResponseEntity<List<TaleResponseDto>> getUnlearnedTalesTop3ByMemberId(@PathVariable Long memberId) {
//        List<TaleResponseDto> posts = tale_service.getUnlearnedTaleTop3ByMemberId(memberId);
//        return ResponseEntity.ok(posts);
//    }

    @Operation(summary = "#005 완료하지 않은 동화 전체 조회", description = "학습을 완료하지 않은 동화를 최신순으로 불러오는 API입니다.")
    @GetMapping("/unlearned/{memberId}")
    public ResponseEntity<List<TaleResponseDto>> getUnlearnedTalesByMemberId(@PathVariable Long memberId,@RequestParam(name = "count", required = false) Integer count) {
        List<TaleResponseDto> posts = tale_service.getUnlearnedTaleByMemberId(memberId,count);
        return ResponseEntity.ok(posts);
    }

//    @Operation(summary = "#006 최근 학습한 동화 미리보기 불러오기", description = "최근 학습한 동화 중 최신 동화를 3개 불러오는 API입니다.")
//    @GetMapping("/studied/{memberId}")
//    public ResponseEntity<List<TaleResponseDto>> getStudiedTalesTop3ByMemberId(@PathVariable Long memberId) {
//        List<TaleResponseDto> posts = tale_service.getStudiedTaleTop3ByMemberId(memberId);
//        return ResponseEntity.ok(posts);
//    }

    @Operation(summary = "#007 최근 학습한 동화 전체 조회", description = "최근 학습한 동화를 최신순으로 불러오는 API입니다.")
    @GetMapping("/studied/top3/{memberId}")
    public ResponseEntity<List<TaleResponseDto>> getStudiedTalesByMemberId(@PathVariable Long memberId,@RequestParam(name = "count", required = false) Integer count) {
        List<TaleResponseDto> posts = tale_service.getStudiedTaleByMemberId(memberId,count);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "#003 단어장 조회", description = "단어장을 조회하는 API입니다.")
    @GetMapping("/word/{memberId}")
    public ResponseEntity<List<WordResponseDto>> getWordByMemberId(@PathVariable Long memberId, @RequestParam(name = "count", required = false) Integer count) {
        List<WordResponseDto> words = tale_service.getWordByMemberId(memberId,count);
        return ResponseEntity.ok(words);
    }


}