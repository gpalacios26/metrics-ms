package com.gpc.metrics.service;

import static org.junit.jupiter.api.Assertions.*;

import com.gpc.metrics.dto.MetricsDateDTO;
import com.gpc.metrics.dto.MetricsFilterDTO;
import com.gpc.metrics.dto.MetricsTimeDTO;
import com.gpc.metrics.model.Temperature;
import com.gpc.metrics.repository.TemperatureRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TemperatureServiceImplTest {

  @InjectMocks
  private TemperatureServiceImpl service;

  @Mock
  private TemperatureRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFindAll() {
    // Arrange
    Temperature temp1 = new Temperature();
    Temperature temp2 = new Temperature();
    Mockito.when(repository.findAll()).thenReturn(Flux.just(temp1, temp2));

    // Act
    List<Temperature> result = service.findAll().collectList().block();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    Mockito.verify(repository, Mockito.times(1)).findAll();
  }

  @Test
  void testSave() {
    // Arrange
    Temperature temperature = new Temperature();
    temperature.setDateTime(LocalDateTime.now());
    temperature.setTimeRange("12:00 - 13:00");
    Temperature savedTemperature = new Temperature();
    savedTemperature.setTimeRange("12:00 - 13:00");
    Mockito.when(repository.save(Mockito.any(Temperature.class))).thenReturn(Mono.just(savedTemperature));

    // Act
    Temperature result = service.save(temperature).block();

    // Assert
    assertNotNull(result);
    assertEquals("12:00 - 13:00", result.getTimeRange());
    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Temperature.class));
  }

  @Test
  void testSaveWithTimeRangeNull() {
    // Arrange
    Temperature temperature = new Temperature();
    temperature.setDateTime(LocalDateTime.now());
    temperature.setTimeRange(null);
    Temperature savedTemperature = new Temperature();
    savedTemperature.setTimeRange("12:00 - 13:00");
    Mockito.when(repository.save(Mockito.any(Temperature.class))).thenReturn(Mono.just(savedTemperature));

    // Act
    Temperature result = service.save(temperature).block();

    // Assert
    assertNotNull(result);
    assertEquals("12:00 - 13:00", result.getTimeRange());
    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Temperature.class));
  }

  @Test
  void testFindMetricsTime() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    filterDTO.setDate(LocalDate.now());
    filterDTO.setType("C");

    Temperature temp1 = new Temperature();
    temp1.setTimeRange("12:00 - 13:00");
    temp1.setValueCelsius(20.0);

    Temperature temp2 = new Temperature();
    temp2.setTimeRange("12:00 - 13:00");
    temp2.setValueCelsius(25.0);

    Temperature temp3 = new Temperature();
    temp3.setTimeRange("13:00 - 14:00");
    temp3.setValueCelsius(30.0);

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
        .thenReturn(Flux.just(temp1, temp2, temp3));

    // Act
    List<MetricsTimeDTO> result = service.findMetricsTime(filterDTO).collectList().block();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());

    MetricsTimeDTO firstMetric = result.get(0);
    assertEquals("12:00 - 13:00", firstMetric.getTime());
    assertEquals(20.0, firstMetric.getMin());
    assertEquals(25.0, firstMetric.getMax());
    assertEquals(22.5, firstMetric.getAverage());

    MetricsTimeDTO secondMetric = result.get(1);
    assertEquals("13:00 - 14:00", secondMetric.getTime());
    assertEquals(30.0, secondMetric.getMin());
    assertEquals(30.0, secondMetric.getMax());
    assertEquals(30.0, secondMetric.getAverage());

    Mockito.verify(repository, Mockito.times(1)).findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
  }

  @Test
  void testFindMetricsTimeWithFahrenheit() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    filterDTO.setDate(LocalDate.now());
    filterDTO.setType("F");

    Temperature temp1 = new Temperature();
    temp1.setTimeRange("12:00 - 13:00");
    temp1.setValueCelsius(20.0); // Celsius

    Temperature temp2 = new Temperature();
    temp2.setTimeRange("12:00 - 13:00");
    temp2.setValueCelsius(25.0); // Celsius

    Temperature temp3 = new Temperature();
    temp3.setTimeRange("13:00 - 14:00");
    temp3.setValueCelsius(30.0); // Celsius

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
        .thenReturn(Flux.just(temp1, temp2, temp3));

    // Act
    List<MetricsTimeDTO> result = service.findMetricsTime(filterDTO).collectList().block();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());

    MetricsTimeDTO firstMetric = result.get(0);
    assertEquals("12:00 - 13:00", firstMetric.getTime());
    assertEquals(68.0, firstMetric.getMin()); // 20°C -> 68°F
    assertEquals(77.0, firstMetric.getMax()); // 25°C -> 77°F
    assertEquals(72.5, firstMetric.getAverage()); // 22.5°C -> 72.5°F

    MetricsTimeDTO secondMetric = result.get(1);
    assertEquals("13:00 - 14:00", secondMetric.getTime());
    assertEquals(86.0, secondMetric.getMin()); // 30°C -> 86°F
    assertEquals(86.0, secondMetric.getMax());
    assertEquals(86.0, secondMetric.getAverage());

    Mockito.verify(repository, Mockito.times(1)).findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
  }

  @Test
  void testFindMetricsDate() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    filterDTO.setDate(LocalDate.now());
    filterDTO.setType("C");

    Temperature temp1 = new Temperature();
    temp1.setValueCelsius(20.0);

    Temperature temp2 = new Temperature();
    temp2.setValueCelsius(25.0);

    Temperature temp3 = new Temperature();
    temp3.setValueCelsius(30.0);

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
        .thenReturn(Flux.just(temp1, temp2, temp3));

    // Act
    MetricsDateDTO result = service.findMetricsDate(filterDTO).block();

    // Assert
    assertNotNull(result);
    assertEquals(LocalDate.now().toString(), result.getDate());
    assertEquals(20.0, result.getMin());
    assertEquals(30.0, result.getMax());
    assertEquals(25.0, result.getAverage());

    Mockito.verify(repository, Mockito.times(1)).findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
  }

  @Test
  void testFindMetricsDateWithFahrenheit() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    filterDTO.setDate(LocalDate.now());
    filterDTO.setType("F");

    Temperature temp1 = new Temperature();
    temp1.setValueCelsius(20.0); // Celsius

    Temperature temp2 = new Temperature();
    temp2.setValueCelsius(25.0); // Celsius

    Temperature temp3 = new Temperature();
    temp3.setValueCelsius(30.0); // Celsius

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
        .thenReturn(Flux.just(temp1, temp2, temp3));

    // Act
    MetricsDateDTO result = service.findMetricsDate(filterDTO).block();

    // Assert
    assertNotNull(result);
    assertEquals(LocalDate.now().toString(), result.getDate());
    assertEquals(68.0, result.getMin()); // 20°C -> 68°F
    assertEquals(86.0, result.getMax()); // 30°C -> 86°F
    assertEquals(77.0, result.getAverage()); // 25°C -> 77°F

    Mockito.verify(repository, Mockito.times(1)).findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
  }

  @Test
  void testFindMetricsDateWithInvalidType() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    filterDTO.setDate(LocalDate.now());
    filterDTO.setType("N"); // Invalid type

    Temperature temp1 = new Temperature();
    temp1.setValueCelsius(20.0); // Celsius

    Temperature temp2 = new Temperature();
    temp2.setValueCelsius(25.0); // Celsius

    Temperature temp3 = new Temperature();
    temp3.setValueCelsius(30.0); // Celsius

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
        .thenReturn(Flux.just(temp1, temp2, temp3));

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> {
      service.findMetricsDate(filterDTO).block();
    });
  }
}