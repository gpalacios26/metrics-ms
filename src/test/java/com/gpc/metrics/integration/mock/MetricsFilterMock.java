package com.gpc.metrics.integration.mock;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsFilterMock {

  private LocalDate date;

  private String type;
}
