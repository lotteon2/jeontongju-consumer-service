package com.jeontongju.consumer.controller;

import com.jeontongju.consumer.dto.request.FCMTokenForMessagingDto;
import com.jeontongju.consumer.dto.request.OrderPriceForCheckValidRequestDto;
import com.jeontongju.consumer.dto.request.ProfileImageUrlForModifyRequestDto;
import com.jeontongju.consumer.dto.response.*;
import com.jeontongju.consumer.dto.response.ConsumerInfoForInquiryResponseDto;
import com.jeontongju.consumer.service.ConsumerService;
import com.jeontongju.consumer.service.SubscriptionService;
import io.github.bitbox.bitbox.dto.AgeDistributionForShowResponseDto;
import io.github.bitbox.bitbox.dto.ResponseFormat;
import io.github.bitbox.bitbox.enums.MemberRoleEnum;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsumerRestController {

  private final ConsumerService consumerService;
  private final SubscriptionService subscriptionService;

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

  @GetMapping("/consumers-list")
  public ResponseEntity<ResponseFormat<Page<ConsumerDetailForSingleInquiryResponseDto>>>
      getMembersForListLookup(
          @RequestHeader MemberRoleEnum memberRole,
          @RequestParam int page,
          @RequestParam int size) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Page<ConsumerDetailForSingleInquiryResponseDto>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("회원 목록 조회 성공")
                .data(consumerService.getMembersForListLookup(memberRole, page, size))
                .build());
  }

  @PatchMapping("/consumers")
  public ResponseEntity<ResponseFormat<Void>> modifyMyInfo(
      @RequestHeader Long memberId,
      @Valid @RequestBody ProfileImageUrlForModifyRequestDto modifyRequestDto) {

    consumerService.modifyMyInfo(memberId, modifyRequestDto);
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("소비자 개인정보 수정 성공")
                .build());
  }

  @GetMapping("/consumers/{consumerId}")
  public ResponseEntity<ResponseFormat<SpecificConsumerDetailForInquiryResponseDto>>
      getConsumerDetailForInquiry(
          @PathVariable Long consumerId, @RequestHeader MemberRoleEnum memberRole) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<SpecificConsumerDetailForInquiryResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("회원 상세 조회 성공")
                .data(consumerService.getConsumerDetailForInquiry(consumerId, memberRole))
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

  @GetMapping("/consumers/subscription-history")
  public ResponseEntity<ResponseFormat<Page<SubscriptionPaymentsInfoForInquiryResponseDto>>>
      getMySubscriptionHistories(
          @RequestHeader Long memberId,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "10") int size) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Page<SubscriptionPaymentsInfoForInquiryResponseDto>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("구독 결제 내역 조회 성공")
                .data(subscriptionService.getMySubscriptionHistories(memberId, page, size))
                .build());
  }

  @GetMapping("/consumers/my-info")
  public ResponseEntity<ResponseFormat<MyInfoAfterSignInForResponseDto>> getMyInfoAfterSignIn(
      @RequestHeader Long memberId) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<MyInfoAfterSignInForResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("로그인 직후, 내 정보 조회 성공")
                .data(consumerService.getMyInfoAfterSignIn(memberId))
                .build());
  }

  @DeleteMapping("/consumers/subscription")
  public ResponseEntity<ResponseFormat<Void>> unsubscribe(@RequestHeader Long memberId) {

    consumerService.unsubscribe(memberId);
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("구독 해지 성공")
                .build());
  }

  @GetMapping("/consumers/subscriptions/benefit")
  public ResponseEntity<ResponseFormat<SubscriptionBenefitForInquiryResponseDto>>
      getSubscriptionBenefit(@RequestHeader Long memberId) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<SubscriptionBenefitForInquiryResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("구독권 혜택 조회 성공")
                .data(consumerService.getSubscriptionBenefit(memberId))
                .build());
  }

  @PostMapping("/consumers/points-available")
  public ResponseEntity<ResponseFormat<AvailablePointsAtOrderResponseDto>>
      getAvailablePointsAtOrder(
          @RequestHeader Long memberId,
          @Valid @RequestBody OrderPriceForCheckValidRequestDto checkValidRequestDto) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<AvailablePointsAtOrderResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("사용 가능한 내 포인트 조회 성공")
                .data(consumerService.getAvailablePointsAtOrder(memberId, checkValidRequestDto))
                .build());
  }

  @GetMapping("/sellers/consumers/age-analysis")
  public ResponseEntity<ResponseFormat<AgeDistributionForShowResponseDto>> getAgeDistribution(
      @RequestHeader Long memberId, @RequestHeader MemberRoleEnum memberRole) {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<AgeDistributionForShowResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("셀러별, 상품 구매한 소비자 연령 분포 조회 성공")
                .data(consumerService.getAgeDistribution(memberId, memberRole))
                .build());
  }

  @PatchMapping("/fcm-token")
  public ResponseEntity<ResponseFormat<Void>> saveFCMToken(@RequestHeader Long memberId, @RequestBody FCMTokenForMessagingDto fcmTokenDto) {

    consumerService.saveFCMToken(memberId, fcmTokenDto);
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .build());
  }

  @PostMapping("/consumers/coupons")
  public ResponseEntity<ResponseFormat<Void>> receivePromotionCoupon(@RequestHeader Long memberId) {

    consumerService.apply(memberId);
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("쿠폰 수령 성공")
                .build());
  }

  @PatchMapping("/coupons/test/{memberId}")
  public ResponseEntity<ResponseFormat<Void>> getCouponTest3(@PathVariable Long memberId) {

    consumerService.apply(memberId);
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .detail("[테스트] 쿠폰 수령 성공")
                .build());
  }

  @PatchMapping("/coupons/test/reset")
  public ResponseEntity<ResponseFormat<Void>> undoCouponSetting() {

    consumerService.undo();
    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .build());
  }

  @GetMapping("/coupon/test/reset")
  public ResponseEntity<ResponseFormat<Boolean>> getCouponSetting() {

    return ResponseEntity.ok()
        .body(
            ResponseFormat.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .data(consumerService.getCouponSetting())
                .build());
  }
}
