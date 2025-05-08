package com.gpc.metrics.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gpc.metrics.util.CustomDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemperatureDTO {

  private Long id;

  @NotNull(message = "El campo value celsius no puede ser nulo")
  private Double valueCelsius;

  @NotNull(message = "El campo date time no puede ser nulo")
  @JsonDeserialize(using = CustomDateTimeDeserializer.class)
  private LocalDateTime dateTime;

  private String timeRange;
}
