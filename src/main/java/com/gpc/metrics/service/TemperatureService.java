package com.gpc.metrics.service;

import com.gpc.metrics.dto.MetricsDateDTO;
import com.gpc.metrics.dto.MetricsFilterDTO;
import com.gpc.metrics.dto.MetricsTimeDTO;
import com.gpc.metrics.model.Temperature;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TemperatureService {

  Flux<Temperature> findAll();

  Mono<Temperature> save(Temperature temperature);

  Flux<MetricsTimeDTO> findMetricsTime(MetricsFilterDTO dto);

  Mono<MetricsDateDTO> findMetricsDate(MetricsFilterDTO dto);
}
