package com.jeontongju.consumer.controller;

import com.jeontongju.consumer.dto.response.ConsumerInfoForInquiryResponseDto;
import com.jeontongju.consumer.dto.response.PointCreditForInquiryResponseDto;
import com.jeontongju.consumer.dto.response.PointTradeInfoForSummaryNDetailsResponseDto;
import com.jeontongju.consumer.service.ConsumerService;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsumerRestController {

  private final ConsumerService consumerService;

  @GetMapping("/consumers")
  public ResponseEntity<ResponseFormat<ConsumerInfoForInquiryResponseDto>> getMyInfo(
      @RequestHeader Long memberId) {

    ConsumerInfoForInquiryResponseDto myInfoResponseDto = consumerService.getMyInfo(memberId);
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<ConsumerInfoForInquiryResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("소비자 개인정보 조회 성공")
                .data(myInfoResponseDto)
                .build());
  }

  @GetMapping("/consumers/point-history")
  public ResponseEntity<ResponseFormat<PointTradeInfoForSummaryNDetailsResponseDto>>
      getMyPointHistories(
          @RequestHeader Long memberId,
          @RequestParam(value = "search", required = false) String search,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "10") int size) {

    PointTradeInfoForSummaryNDetailsResponseDto myPointSummaryNDetails = null;
    if ("acc".equals(search)) {
      myPointSummaryNDetails =
          consumerService.getMyPointSummaryNSavingDetails(memberId, page, size);
    } else if ("use".equals(search)) {
      myPointSummaryNDetails = consumerService.getMyPointSummaryNUseDetails(memberId, page, size);
    } else {
      myPointSummaryNDetails = consumerService.getMyPointSummaryNDetails(memberId, page, size);
    }
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<PointTradeInfoForSummaryNDetailsResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("포인트 거래내역 조회 성공")
                .data(myPointSummaryNDetails)
                .build());
  }

  @GetMapping("/consumers/point-credit")
  public ResponseEntity<ResponseFormat<PointCreditForInquiryResponseDto>> getMyPointNCredit(
      @RequestHeader Long memberId) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<PointCreditForInquiryResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("포인트 + 크레딧 조회 성공")
                .data(consumerService.getPointNCredit(memberId))
                .build());
  }
}
