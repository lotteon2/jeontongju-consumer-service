package com.jeontongju.consumer.utils;

public interface CustomErrMessage {

  String NOT_FOUND_CONSUMER = "찾을 수 없는 소비자";
  String ERROR_KAFKA = "카프카 예외 발생";
  String INSUFFICIENT_POINT = "부족한 포인트";
  String INSUFFICIENT_CREDIT = "부족한 크레딧";
  String POINT_USAGE_OVER_10_PERCENTAGE = "포인트 사용액 10% 초과";
  String NOT_FOUND_ADDRESS = "찾을 수 없는 주소지";
  String NOT_FOUND_DEFAULT_ADDRESS = "찾을 수 없는 기본 주소지";
  String UNSUBSCRIBED_CONSUMER = "구독하지 않은 소비자";
  String ALREADY_RECEIVE_COUPON = "이미 수령한 쿠폰";
  String EXHAUSTED_COUPON = "쿠폰 소진";
  String NOT_OPEN_PROMOTION_COUPON_EVENT = "프로모션 쿠폰 이벤트 시각 미오픈";
  String NOT_ADMIN_ACCESS_DENIED = "관리자만 접근 허용";
  String NOT_SELLER_ACCESS_DENIED = "셀러만 접근 허용";
}
