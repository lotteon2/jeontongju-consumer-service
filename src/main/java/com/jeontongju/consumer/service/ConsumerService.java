package com.jeontongju.consumer.service;

import com.jeontongju.consumer.domain.Consumer;
import com.jeontongju.consumer.domain.PointHistory;
import com.jeontongju.consumer.dto.response.ConsumerInfoForInquiryResponseDto;
import com.jeontongju.consumer.dto.response.PointTradeInfoForSummaryNDetailsResponseDto;
import com.jeontongju.consumer.dto.response.PointTradeInfoForSingleInquiryResponseDto;
import com.jeontongju.consumer.dto.temp.ConsumerInfoForAuctionResponse;
import com.jeontongju.consumer.dto.temp.ConsumerInfoForCreateBySnsRequestDto;
import com.jeontongju.consumer.dto.temp.ConsumerInfoForCreateRequestDto;
import com.jeontongju.consumer.exception.*;
import com.jeontongju.consumer.kafka.ConsumerProducer;
import com.jeontongju.consumer.mapper.ConsumerMapper;
import com.jeontongju.consumer.mapper.HistoryMapper;
import com.jeontongju.consumer.repository.ConsumerRepository;
import com.jeontongju.consumer.utils.CustomErrMessage;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.dto.UserPointUpdateDto;
import io.github.bitbox.bitbox.util.KafkaTopicNameInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumerService {

  private final ConsumerRepository consumerRepository;
  private final HistoryService historyService;
  private final ConsumerMapper consumerMapper;
  private final HistoryMapper historyMapper;
  private final ConsumerProducer consumerProducer;

  @Transactional
  public void createConsumerForSignup(ConsumerInfoForCreateRequestDto createRequestDto) {
    consumerRepository.save(consumerMapper.toEntity(createRequestDto));
  }

  @Transactional
  public void createConsumerForCreateBySns(
      ConsumerInfoForCreateBySnsRequestDto createBySnsRequestDto) {
    consumerRepository.save(consumerMapper.toEntity(createBySnsRequestDto));
  }

  /**
   * 주문 및 결제 확정을 위한 포인트 차감
   *
   * @param orderInfoDto
   */
  @Transactional
  public void consumePoint(OrderInfoDto orderInfoDto) throws KafkaException {

    log.info("ConsumerService's consumePoint executes..");
    UserPointUpdateDto userPointUpdateDto = orderInfoDto.getUserPointUpdateDto();

    if (userPointUpdateDto.getPoint() != null) {
      Consumer foundConsumer = getConsumer(userPointUpdateDto.getConsumerId());

      checkPointPolicy(
          foundConsumer, userPointUpdateDto.getPoint(), userPointUpdateDto.getTotalAmount());
      foundConsumer.consumePoint(foundConsumer.getPoint() - userPointUpdateDto.getPoint());
    }

    log.info("ConsumerService's consumePoint Successful executed!");
    consumerProducer.sendUpdateCoupon(KafkaTopicNameInfo.USE_COUPON, orderInfoDto);
  }

  /**
   * 포인트 사용 정책 만족 여부 확인
   *
   * @param foundConsumer
   * @param point
   * @param totalAmount
   */
  public void checkPointPolicy(Consumer foundConsumer, Long point, Long totalAmount)
      throws PointInsufficientException, PointUsageOver10PercetageException {

    Long currentPoint = foundConsumer.getPoint();
    if (currentPoint < point) { // 포인트 확인
      throw new PointInsufficientException(CustomErrMessage.INSUFFICIENT_POINT);
    }

    // 포인트 사용 정책 확인
    if (point > totalAmount * 0.1) {
      throw new PointUsageOver10PercetageException(CustomErrMessage.POINT_USAGE_OVER_10_PERCENTAGE);
    }
  }

  /**
   * 주문 및 결제 로직에서 에러 발생 시, 포인트 롤백
   *
   * @param orderInfoDto
   */
  @Transactional
  public void rollbackPoint(OrderInfoDto orderInfoDto) {

    UserPointUpdateDto userPointUpdateDto = orderInfoDto.getUserPointUpdateDto();
    if (userPointUpdateDto.getPoint() != null) {
      Consumer foundConsumer = getConsumer(userPointUpdateDto.getConsumerId());
      foundConsumer.rollbackPoint(foundConsumer.getPoint() + userPointUpdateDto.getPoint());
    }
  }

  public void hasPoint(UserPointUpdateDto userPointUpdateDto) {

    Consumer foundConsumer = getConsumer(userPointUpdateDto.getConsumerId());
    checkPointPolicy(
        foundConsumer, userPointUpdateDto.getPoint(), userPointUpdateDto.getTotalAmount());
  }

  public ConsumerInfoForAuctionResponse getConsumerInfoForAuction(Long consumerId) {

    Consumer foundConsumer = getConsumer(consumerId);
    return ConsumerInfoForAuctionResponse.toDto(foundConsumer);
  }

  @Transactional
  public void consumeCreditByBidding(Long consumerId, Long deductionCredit) {

    Consumer consumer = getConsumer(consumerId);
    if (consumer.getAuctionCredit() < deductionCredit) {
      throw new InsufficientCreditException(CustomErrMessage.INSUFFICIENT_CREDIT);
    }

    consumer.assignAuctionCredit(consumer.getAuctionCredit() - deductionCredit);
  }

  public ConsumerInfoForInquiryResponseDto getMyInfo(Long memberId) {

    Consumer foundConsumer = getConsumer(memberId);
    return consumerMapper.toInquiryDto(foundConsumer);
  }

  public PointTradeInfoForSummaryNDetailsResponseDto getMyPointSummaryNDetails(
      Long consumerId, int page, int size) {

    Consumer foundConsumer = getConsumer(consumerId);

    // 포인트 거래내역 가져오기 (한 페이지 만큼)
    Page<PointTradeInfoForSingleInquiryResponseDto> pointHistoriesPaged =
        historyService.getPointHistoriesPaged(foundConsumer, page, size);

    // 포인트 요약 정보 계산하기
    return calcPointSummary(foundConsumer, pointHistoriesPaged);
  }

  public PointTradeInfoForSummaryNDetailsResponseDto getMyPointSummaryNSavingDetails(
      Long consumerId, int page, int size) {

    Consumer foundConsumer = getConsumer(consumerId);

    Page<PointTradeInfoForSingleInquiryResponseDto> pointSavingHistoriesPaged =
        historyService.getPointSavingHistoriesPaged(foundConsumer, page, size);

    return calcPointSummary(foundConsumer, pointSavingHistoriesPaged);
  }

  public PointTradeInfoForSummaryNDetailsResponseDto getMyPointSummaryNUseDetails(Long consumerId, int page, int size) {

    Consumer foundConsumer = getConsumer(consumerId);

    Page<PointTradeInfoForSingleInquiryResponseDto> pointSavingHistoriesPaged =
            historyService.getPointUseHistoriesPaged(foundConsumer, page, size);

    return calcPointSummary(foundConsumer, pointSavingHistoriesPaged);
  }

  private PointTradeInfoForSummaryNDetailsResponseDto calcPointSummary(
      Consumer consumer, Page<PointTradeInfoForSingleInquiryResponseDto> pointHistoriesPaged) {

    List<PointHistory> allPointHistories = historyService.getAllPointHistories(consumer);
    long[] summary = historyService.calcTotalPointSummary(allPointHistories);

    return historyMapper.toPointSummaryNDetailsResponseDto(
        consumer.getPoint(), summary[0], summary[1], pointHistoriesPaged);
  }

  /**
   * consumerId로 Consumer 찾기 (공통화)
   *
   * @param consumerId
   * @return Consumer
   */
  private Consumer getConsumer(Long consumerId) {

    return consumerRepository
        .findByConsumerId(consumerId)
        .orElseThrow(() -> new ConsumerNotFoundException(CustomErrMessage.NOT_FOUND_CONSUMER));
  }


}
