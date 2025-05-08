package com.gpc.metrics.controller;

import com.gpc.metrics.dto.MetricsDateDTO;
import com.gpc.metrics.dto.MetricsFilterDTO;
import com.gpc.metrics.dto.MetricsTimeDTO;
import com.gpc.metrics.dto.TemperatureDTO;
import com.gpc.metrics.model.Temperature;
import com.gpc.metrics.service.TemperatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/temperatures")
@RequiredArgsConstructor
@Tag(name = "Temperature API", description = "Documentación de la API de Temperatura")
public class TemperatureController {

  private final TemperatureService service;

  private final ModelMapper modelMapper;

  @GetMapping
  @Operation(summary = "Listar Temperaturas", description = "Devuelve todos los registros de temperatura")
  public Mono<ResponseEntity<Flux<TemperatureDTO>>> findAll() {
    Flux<TemperatureDTO> fx = service.findAll().map(this::convertToDto);
    return Mono.just(ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fx)
    ).defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(summary = "Crear Temperatura", description = "Registro de una temperatura")
  public Mono<ResponseEntity<TemperatureDTO>> create(@Valid @RequestBody TemperatureDTO dto, final ServerHttpRequest req) {
    return service.save(this.convertToModel(dto))
        .map(model -> ResponseEntity
            .created(URI.create(req.getURI().toString().concat("/").concat(String.valueOf(model.getId()))))
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.convertToDto(model))
        ).defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/metrics-time")
  @Operation(summary = "Métricas Temperatura - Tiempo", description = "Devuelve las métricas de temperatura por tiempo y con filtros")
  public Mono<ResponseEntity<Flux<MetricsTimeDTO>>> findMetricsTime(@Valid @ModelAttribute MetricsFilterDTO dto) {
    Flux<MetricsTimeDTO> fx = service.findMetricsTime(dto);
    return Mono.just(ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fx)
    ).defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/metrics-date")
  @Operation(summary = "Métricas Temperatura - Fecha", description = "Devuelve las métricas de temperatura por fecha y con filtros")
  public Mono<ResponseEntity<MetricsDateDTO>> findMetricsDate(@Valid @ModelAttribute MetricsFilterDTO dto) {
    return service.findMetricsDate(dto)
        .map(metrics -> ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(metrics)
        )
    .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  private TemperatureDTO convertToDto(Temperature model) {
    return modelMapper.map(model, TemperatureDTO.class);
  }

  private Temperature convertToModel(TemperatureDTO dto) {
    return modelMapper.map(dto, Temperature.class);
  }
}
