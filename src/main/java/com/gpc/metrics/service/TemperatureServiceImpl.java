package com.gpc.metrics.service;

import com.gpc.metrics.dto.MetricsDateDTO;
import com.gpc.metrics.dto.MetricsFilterDTO;
import com.gpc.metrics.dto.MetricsTimeDTO;
import com.gpc.metrics.model.Temperature;
import com.gpc.metrics.repository.TemperatureRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TemperatureServiceImpl implements TemperatureService {

  private final TemperatureRepository repository;

  @Override
  public Flux<Temperature> findAll() {
    return repository.findAll();
  }

  @Override
  public Mono<Temperature> save(Temperature temperature) {
    if (temperature.getTimeRange() == null) {
      temperature.setTimeRange(getHourRange(temperature.getDateTime()));
    }
    return repository.save(temperature);
  }

  @Override
  public Flux<MetricsTimeDTO> findMetricsTime(MetricsFilterDTO dto) {
    return findByDate(dto.getDate())
            .groupBy(Temperature::getTimeRange)
            .flatMap(groupedFlux -> groupedFlux.collectList()
                .map(temperatures -> {
                  double min = temperatures.stream().mapToDouble(Temperature::getValueCelsius).min().orElse(0);
                  double max = temperatures.stream().mapToDouble(Temperature::getValueCelsius).max().orElse(0);
                  double average = temperatures.stream().mapToDouble(Temperature::getValueCelsius).average().orElse(0);
                  return new MetricsTimeDTO(groupedFlux.key(), getValueByType(min, dto.getType()), getValueByType(max, dto.getType()), getValueByType(average, dto.getType()));
                }));
  }

  @Override
  public Mono<MetricsDateDTO> findMetricsDate(MetricsFilterDTO dto) {
    return findByDate(dto.getDate())
        .collectList()
        .flatMap(temperatures -> {
          double min = temperatures.stream().mapToDouble(Temperature::getValueCelsius).min().orElse(0);
          double max = temperatures.stream().mapToDouble(Temperature::getValueCelsius).max().orElse(0);
          double average = temperatures.stream().mapToDouble(Temperature::getValueCelsius).average().orElse(0);
          MetricsDateDTO metrics = new MetricsDateDTO(dto.getDate().toString(), getValueByType(min, dto.getType()), getValueByType(max, dto.getType()), getValueByType(average, dto.getType()));
          return Mono.just(metrics);
        });
  }

  private String getHourRange(LocalDateTime dateTime) {
    int hour = dateTime.getHour();
    int nextHour = (hour + 1) % 24;
    return String.format("%02d:00 - %02d:00", hour, nextHour);
  }

  private Flux<Temperature> findByDate(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(23, 59, 59);
    return repository.findByDateTimeBetween(startOfDay, endOfDay);
  }

  private Double getValueByType(Double value, String type) {
    return switch (type) {
      case "C" -> getValueRound(value);
      case "F" -> getValueRound(convertFahrenheit(value));
      default -> throw new IllegalStateException("Unexpected value: " + type);
    };
  }

  private Double getValueRound(Double value) {
    BigDecimal result = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    return result.doubleValue();
  }

  private Double convertFahrenheit(Double celsius) {
    return (celsius * 9 / 5) + 32;
  }
}
