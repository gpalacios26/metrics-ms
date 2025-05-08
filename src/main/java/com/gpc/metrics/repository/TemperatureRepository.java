package com.gpc.metrics.repository;

import com.gpc.metrics.model.Temperature;
import java.time.LocalDateTime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TemperatureRepository extends ReactiveCrudRepository<Temperature, Long> {

  Flux<Temperature> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
