package com.jeontongju.consumer.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PointTradeInfoForSummaryNDetailsResponseDto {

  private Long point;
  private Long totalAcc;
  private Long totalUse;
  private Page<PointTradeInfoForSingleInquiryResponseDto> histories;
}
