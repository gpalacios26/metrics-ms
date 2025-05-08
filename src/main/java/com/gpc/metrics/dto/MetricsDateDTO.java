package com.gpc.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDateDTO {

  private String date;

  private Double min;

  private Double max;

  private Double average;
}
