package com.jeontongju.consumer.domain;

import com.jeontongju.consumer.domain.common.BaseEntity;
import com.jeontongju.consumer.dto.temp.TradePathEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "credit_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CreditHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "trade_credit_id")
  private Long tradeCreditId;

  @ManyToOne
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  @Column(name = "trade_point", nullable = false)
  private Long tradePoint;

  @Enumerated(EnumType.STRING)
  @Column(name = "trade_path", nullable = false)
  private TradePathEnum tradePath;
}
