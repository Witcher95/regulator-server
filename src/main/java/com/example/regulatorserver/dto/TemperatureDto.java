package com.example.regulatorserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemperatureDto {
    private Float temperature;

    public TemperatureDto(Float temperature) {
        this.temperature = temperature;
    }
}
