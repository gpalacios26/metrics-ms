package com.gpc.metrics.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gpc.metrics.util.CustomDateDeserializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsFilterDTO {

  @NotNull(message = "El campo date no puede ser nulo")
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private LocalDate date;

  @NotNull(message = "El campo type no puede ser nulo")
  @Pattern(regexp = "C|F", message = "El campo type debe ser 'C' o 'F'")
  private String type;
}
