package com.gpc.metrics.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("temperature")
public class Temperature {

  @Id
  private Long id;

  @Column("value_celsius")
  private Double valueCelsius;

  @Column("date_time")
  private LocalDateTime dateTime;

  @Column("time_range")
  private String timeRange;
}
