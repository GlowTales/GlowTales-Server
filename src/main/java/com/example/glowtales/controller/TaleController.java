package com.example.glowtales.controller;

import com.example.glowtales.dto.request.TaleRequest;
import com.example.glowtales.dto.response.*;
import com.example.glowtales.service.TaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tale")
@Tag(name = "Tale API", description = "API for managing tales")
public class TaleController {


//
//#011 동화의 퀴즈와 정답 불러오기

//#013 사용자 학습 언어와 수준 조회 GET
//#014 단일 동화 다국어로 조회 GET
//result 사용해서 응답 형식 통일하기
// token->member 갈아끼기


    private final TaleService taleService;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(TaleController.class);

    @Operation(summary = "#001 전체 동화 상태창 조회", description = "홈 화면에 나타나는 상태를 불러오는 API입니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<HomeInfoResponseDto> getHomeInfoByMemberId(@PathVariable Long memberId) {
        HomeInfoResponseDto infos = taleService.getHomeInfoByMemberId(memberId);
        return ResponseEntity.ok(infos);
    }

    @Operation(summary = "#005 완료하지 않은 동화 조회", description = "학습을 완료하지 않은 동화를 최신순으로 불러오는 API입니다.")
    @GetMapping("/unlearned/{memberId}")
    public ResponseEntity<List<TaleResponseDto>> getUnlearnedTalesByMemberId(@PathVariable Long memberId,@RequestParam(name = "count", required = false) Integer count) {
        logger.info("Count value: {}", count);
        if (count == null) {
            count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
        }
        List<TaleResponseDto> posts = taleService.getUnlearnedTaleByMemberId(memberId,count);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "#007 최근 학습한 동화 전체 조회", description = "최근 학습한 동화를 최신순으로 불러오는 API입니다.")
    @GetMapping("/studied/{memberId}")
    public ResponseEntity<List<TaleResponseDto>> getStudiedTalesByMemberId(@PathVariable Long memberId,@RequestParam(name = "count", required = false) Integer count) {
        logger.info("Count value: {}", count);
        if (count == null) {
            count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
        }
        List<TaleResponseDto> posts = taleService.getStudiedTaleByMemberId(memberId,count);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "#003 단어장 조회", description = "단어장을 조회하는 API입니다.")
    @GetMapping("/word/{memberId}")
    public ResponseEntity<List<WordResponseDto>> getWordByMemberId(@PathVariable Long memberId, @RequestParam(name = "count", required = false) Integer count) {
        if (count == null) {
            count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
        }
        List<WordResponseDto> words = taleService.getWordByMemberId(memberId,count);
        return ResponseEntity.ok(words);
    }

    @Operation(summary = "#010 사진에서 키워드 추출하기", description = "업로드한 사진에서 키워드를 추출하는 API입니다.")
    @PostMapping("/keyword")
    public ResponseEntity<KeywordResponseDto> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String responseJson = taleService.getKeyword(file);
            KeywordResponseDto keywordResponseDto = objectMapper.readValue(responseJson, KeywordResponseDto.class);
            return ResponseEntity.ok(keywordResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @Operation(summary = "#008 동화 만들기", description = "동화를 만드는 API입니다.")
    @PostMapping("/new")
    public ResponseEntity<TaleDetailResponseDto> TaleRequest(@RequestBody TaleRequest request) {

        TaleDetailResponseDto responseDto = TaleDetailResponseDto.builder()
                .taleId(1L)
                .title("재영이와 구름 위의 코끼리") // 예시 제목
                .language("Ko") // 예시 언어
                .story("여름의 어느 날, 재영이는 시원한 바람을 맞으며 공원에 놀러 갔어.\n" +
                        "푸른 하늘을 올려다보던 재영이는 구름 속에서 무언가 움직이는 걸 보았어.\n" +
                        "\"구름 속에 뭐가 있을까?\" 재영이는 호기심이 가득했지.\n" +
                        "갑자기, 구름이 빵빵 터지면서 커다란 코끼리가 하늘에서 내려왔어!\n" +
                        "코끼리는 뾰족한 파란 모자를 쓰고 있었고, 하늘을 떠다니며 춤을 추고 있었지.\n" +
                        "재영이는 눈을 의심하며 \"코끼리가 구름 위에서 춤추고 있어?!\"라고 외쳤어.\n" +
                        "코끼리는 공중에서 솜사탕처럼 둥둥 떠다니며 재영에게 손을 흔들었어.\n" +
                        "\"안녕! 난 클라우드코!\" 코끼리가 신나게 인사했어.\n" +
                        "재영이는 \"클라우드코? 정말 기이한 이름이네!\"라며 웃었어.\n" +
                        "클라우드코는 유쾌하게 웃으며 \"그래! 난 여름 구름의 특별한 코끼리야!\"라고 했어.\n" +
                        "재영이는 \"여름에 구름 위에서 뭐 하는 거야?\"라고 물었어.\n" +
                        "클라우드코는 \"구름 위에서 파티를 열고, 사람들에게 웃음을 주는 게 내 일이야!\"라고 답했어.\n" +
                        "\"진짜? 그럼 나도 파티에 초대해줘!\" 재영이가 신나게 말했어.\n" +
                        "클라우드코는 재영을 구름으로 초대하며 \"그럼, 나와 함께 구름 파티를 즐기자!\"라고 했어.\n" +
                        "재영이는 클라우드코의 등에 올라타서 구름 속으로 올라갔어.\n" +
                        "구름 속에 도착하자, 다양한 색깔의 구름과 기발한 장식들이 있었어.\n" +
                        "사람들은 구름 위에서 춤을 추고, 코끼리들이 연주하는 음악에 맞춰 즐기고 있었지.\n" +
                        "재영이는 구름 위에서 흥겹게 춤을 추며 기분이 좋았어.\n" +
                        "클라우드코는 재영에게 특별한 구름 음료를 건네며 \"이건 구름에서 만든 음료야!\"라고 했어.\n" +
                        "재영이는 음료를 마시며 \"이 음료, 너무 맛있어!\"라고 놀라워했어.\n" +
                        "그때, 구름 위에서 작은 소리가 들렸어. \"도와줘! 나 떨어질 것 같아!\"\n" +
                        "재영이와 클라우드코는 소리가 나는 쪽으로 달려갔어.\n" +
                        "작은 구름이 바람에 휘날리며 떨어지려고 하고 있었어.\n" +
                        "클라우드코는 재영이에게 \"작은 구름을 잡아줘!\"라고 했어.\n" +
                        "재영이는 용감하게 작은 구름을 잡아서 하늘로 다시 올려보냈어.\n" +
                        "작은 구름은 감사하며 \"고마워! 이제 안전해!\"라고 말했어.\n" +
                        "클라우드코는 재영이에게 \"멋진 영웅이네!\"라고 칭찬했어.\n" +
                        "재영이는 부끄러워하며 \"그냥 작은 도움이었을 뿐이야!\"라고 말했어.\n" +
                        "파티가 끝나갈 무렵, 클라우드코는 재영이에게 \"이제 돌아갈 시간이야. 하지만 언제든지 구름 속으로 놀러와!\"라고 했어.\n" +
                        "재영이는 행복하게 고개를 끄덕이며 \"고마워, 클라우드코! 꼭 다시 올게!\"라고 인사하며 구름에서 내려왔어.") // 예시 내용
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}