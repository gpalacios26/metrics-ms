package com.gpc.metrics.util;

import com.gpc.metrics.model.Temperature;
import com.gpc.metrics.repository.TemperatureRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DataInitializer {

  private final TemperatureRepository repository;

  private final DatabaseClient databaseClient;

  @PostConstruct
  public void initData() {
    createTableIfNotExists()
        .thenMany(repository.deleteAll())
        .thenMany(Flux.just(
              new Temperature(null, 25.0, LocalDateTime.of(2025, 5, 6, 10, 20), "10:00 - 11:00"),
              new Temperature(null, 26.0, LocalDateTime.of(2025, 5, 6, 10, 30), "10:00 - 11:00"),
              new Temperature(null, 26.5, LocalDateTime.of(2025, 5, 6, 10, 40), "10:00 - 11:00"),
              new Temperature(null, 30.5, LocalDateTime.of(2025, 5, 6, 11, 45), "11:00 - 12:00"),
              new Temperature(null, 31.0, LocalDateTime.of(2025, 5, 6, 11, 55), "11:00 - 12:00")
          ).flatMap(repository::save)
        ).subscribe(temp -> System.out.println("Inserted: " + temp));
  }

  private Mono<Void> createTableIfNotExists() {
    String createTableSql = """
        CREATE TABLE IF NOT EXISTS temperature (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            value_celsius DOUBLE NOT NULL,
            date_time TIMESTAMP NOT NULL,
            time_range VARCHAR(50)
        )
        """;
    return databaseClient.sql(createTableSql).then();
  }
}
