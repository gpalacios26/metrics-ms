package com.gpc.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsTimeDTO {

  private String time;

  private Double min;

  private Double max;

  private Double average;
}
