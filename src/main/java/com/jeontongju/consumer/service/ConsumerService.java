package com.jeontongju.consumer.service;

import com.jeontongju.consumer.domain.Consumer;
import com.jeontongju.consumer.dto.ConsumerInfoForCreateBySnsRequestDto;
import com.jeontongju.consumer.dto.ConsumerInfoForCreateRequestDto;
import com.jeontongju.consumer.exception.KafkaDuringOrderException;
import com.jeontongju.consumer.kafka.ConsumerProducer;
import com.jeontongju.consumer.mapper.ConsumerMapper;
import com.jeontongju.consumer.repository.ConsumerRepository;
import com.jeontongju.consumer.utils.CustomErrMessage;
import io.github.bitbox.bitbox.dto.OrderInfoDto;
import io.github.bitbox.bitbox.dto.UserPointUpdateDto;
import io.github.bitbox.bitbox.util.KafkaTopicNameInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumerService {

  private final ConsumerRepository consumerRepository;
  private final ConsumerMapper consumerMapper;
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

  @Transactional
  public void consumePoint(OrderInfoDto orderInfoDto) {

    UserPointUpdateDto userPointUpdateDto = orderInfoDto.getUserPointUpdateDto();
    Consumer foundConsumer =
        consumerRepository.findByConsumerId(userPointUpdateDto.getConsumerId()).orElseThrow();
    foundConsumer.consumePoint(userPointUpdateDto.getPoint());

    try {
      consumerProducer.sendUpdateCoupon(KafkaTopicNameInfo.USE_COUPON, orderInfoDto);
    } catch (KafkaException e) {
      throw new KafkaDuringOrderException(CustomErrMessage.ERROR_KAFKA);
    }
  }
}
