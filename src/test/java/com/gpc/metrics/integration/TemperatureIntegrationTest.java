package com.gpc.metrics.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.gpc.metrics.controller.TemperatureController;
import com.gpc.metrics.dto.MetricsDateDTO;
import com.gpc.metrics.dto.MetricsTimeDTO;
import com.gpc.metrics.dto.TemperatureDTO;
import com.gpc.metrics.integration.mock.TemperatureMock;
import com.gpc.metrics.model.Temperature;
import com.gpc.metrics.service.TemperatureService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TemperatureController.class)
class TemperatureIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private TemperatureService service;

  @MockitoBean
  private ModelMapper modelMapper;

  @MockitoBean
  private WebProperties.Resources resources;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFindAll() {
    // Arrange
    Temperature temp1 = new Temperature();
    temp1.setId(1L);
    Temperature temp2 = new Temperature();
    temp2.setId(2L);

    TemperatureDTO dto1 = new TemperatureDTO();
    dto1.setId(1L);
    TemperatureDTO dto2 = new TemperatureDTO();
    dto2.setId(2L);

    Mockito.when(service.findAll()).thenReturn(Flux.just(temp1, temp2));
    Mockito.when(modelMapper.map(temp1, TemperatureDTO.class)).thenReturn(dto1);
    Mockito.when(modelMapper.map(temp2, TemperatureDTO.class)).thenReturn(dto2);

    // Act & Assert
    webTestClient.get()
        .uri("/api/temperatures")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(TemperatureDTO.class)
        .value(response -> {
          StepVerifier.create(Flux.fromIterable(response))
              .expectNext(dto1, dto2)
              .verifyComplete();
        });
  }

  @Test
  void testCreate() {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.parse("2025-05-07T18:14:01.184");
    TemperatureMock mock = new TemperatureMock();
    mock.setId(1L);
    mock.setValueCelsius(25.5);
    mock.setDateTime(dateTime);

    Temperature model = new Temperature();
    model.setId(1L);
    model.setValueCelsius(25.5);
    model.setDateTime(dateTime);

    TemperatureDTO outputDto = new TemperatureDTO();
    outputDto.setId(1L);

    Mockito.when(service.save(Mockito.any())).thenReturn(Mono.just(model));
    Mockito.when(modelMapper.map(model, TemperatureDTO.class)).thenReturn(outputDto);

    // Act & Assert
    webTestClient.post()
        .uri("/api/temperatures")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(mock))
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectHeader().valueMatches("Location", ".*/api/temperatures/1")
        .expectBody(TemperatureDTO.class)
        .value(response -> {
          assertEquals(outputDto.getId(), response.getId());
        });
  }

  @Test
  void testFindMetricsTime() {
    // Arrange
    MetricsTimeDTO metricsTime1 = new MetricsTimeDTO();
    MetricsTimeDTO metricsTime2 = new MetricsTimeDTO();

    Mockito.when(service.findMetricsTime(Mockito.any())).thenReturn(Flux.just(metricsTime1, metricsTime2));

    // Act & Assert
    webTestClient.get()
        .uri("/api/temperatures/metrics-time/search?date=2025-05-07&type=C")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(MetricsTimeDTO.class)
        .value(response -> {
          StepVerifier.create(Flux.fromIterable(response))
              .expectNext(metricsTime1, metricsTime2)
              .verifyComplete();
        });
  }

  @Test
  void testFindMetricsDate() {
    // Arrange
    MetricsDateDTO metricsDate = new MetricsDateDTO();

    Mockito.when(service.findMetricsDate(Mockito.any())).thenReturn(Mono.just(metricsDate));

    // Act & Assert
    webTestClient.get()
        .uri("/api/temperatures/metrics-date/search?date=2025-05-07&type=F")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(MetricsDateDTO.class)
        .value(response -> {
          assertEquals(metricsDate, response);
        });
  }
}