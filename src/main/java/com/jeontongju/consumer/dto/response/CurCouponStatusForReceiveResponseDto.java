package com.jeontongju.consumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CurCouponStatusForReceiveResponseDto {

  private Boolean isSoldOut;
  private Boolean isOpen;
  private Boolean isDuplicated;
}
