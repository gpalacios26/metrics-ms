package com.gpc.metrics.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.gpc.metrics.dto.MetricsDateDTO;
import com.gpc.metrics.dto.MetricsFilterDTO;
import com.gpc.metrics.dto.MetricsTimeDTO;
import com.gpc.metrics.dto.TemperatureDTO;
import com.gpc.metrics.model.Temperature;
import com.gpc.metrics.service.TemperatureService;
import java.net.URI;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TemperatureControllerTest {

  @InjectMocks
  private TemperatureController controller;

  @Mock
  private TemperatureService service;

  @Mock
  private ModelMapper modelMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFindAll() {
    // Arrange
    Temperature temp1 = new Temperature();
    Temperature temp2 = new Temperature();
    TemperatureDTO dto1 = new TemperatureDTO();
    TemperatureDTO dto2 = new TemperatureDTO();

    Mockito.when(service.findAll()).thenReturn(Flux.just(temp1, temp2));
    Mockito.when(modelMapper.map(temp1, TemperatureDTO.class)).thenReturn(dto1);
    Mockito.when(modelMapper.map(temp2, TemperatureDTO.class)).thenReturn(dto2);

    // Act
    Mono<ResponseEntity<Flux<TemperatureDTO>>> response = controller.findAll();

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          StepVerifier.create(Objects.requireNonNull(entity.getBody()))
              .expectNext(dto1, dto2)
              .verifyComplete();
        })
        .verifyComplete();
  }

  @Test
  void testCreate() {
    // Arrange
    TemperatureDTO inputDto = new TemperatureDTO();
    Temperature model = new Temperature();
    TemperatureDTO outputDto = new TemperatureDTO();
    ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);

    Mockito.when(modelMapper.map(inputDto, Temperature.class)).thenReturn(model);
    Mockito.when(service.save(model)).thenReturn(Mono.just(model));
    Mockito.when(modelMapper.map(model, TemperatureDTO.class)).thenReturn(outputDto);
    Mockito.when(request.getURI()).thenReturn(URI.create("http://localhost/api/temperatures"));

    // Act
    Mono<ResponseEntity<TemperatureDTO>> response = controller.create(inputDto, request);

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          assertEquals(outputDto, entity.getBody());
          assertTrue(Objects.requireNonNull(entity.getHeaders().getLocation()).toString().contains("/api/temperatures/"));
        })
        .verifyComplete();
  }

  @Test
  void testFindMetricsTime() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    MetricsTimeDTO metricsTime1 = new MetricsTimeDTO();
    MetricsTimeDTO metricsTime2 = new MetricsTimeDTO();

    Mockito.when(service.findMetricsTime(filterDTO)).thenReturn(Flux.just(metricsTime1, metricsTime2));

    // Act
    Mono<ResponseEntity<Flux<MetricsTimeDTO>>> response = controller.findMetricsTime(filterDTO);

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          StepVerifier.create(Objects.requireNonNull(entity.getBody()))
              .expectNext(metricsTime1, metricsTime2)
              .verifyComplete();
        })
        .verifyComplete();
  }

  @Test
  void testFindMetricsDate() {
    // Arrange
    MetricsFilterDTO filterDTO = new MetricsFilterDTO();
    MetricsDateDTO metricsDate = new MetricsDateDTO();

    Mockito.when(service.findMetricsDate(filterDTO)).thenReturn(Mono.just(metricsDate));

    // Act
    Mono<ResponseEntity<MetricsDateDTO>> response = controller.findMetricsDate(filterDTO);

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          assertEquals(metricsDate, entity.getBody());
        })
        .verifyComplete();
  }
}