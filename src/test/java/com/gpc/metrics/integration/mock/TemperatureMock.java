package com.gpc.metrics.integration.mock;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemperatureMock {

  private Long id;

  private Double valueCelsius;

  private LocalDateTime dateTime;

  private String timeRange;
}
