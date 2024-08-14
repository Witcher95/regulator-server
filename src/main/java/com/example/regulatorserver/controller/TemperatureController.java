package com.example.regulatorserver.controller;

import com.example.regulatorserver.dto.TemperatureDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.regulator.Regulator;
import org.example.regulator.TemperatureRegulator;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Регулятор температуры")
@RestController
@RequestMapping("temperatures")
public class TemperatureController {

    @Operation(summary = "Задать температуру")
    @PostMapping
    public void insert(@RequestBody TemperatureDto temperature) {
        // В двоичном виде:
        // 1-ый бит очищение списка
        // 2-ой бит включение добавления значения в список
        byte enableInsert = (byte) 193;

        Regulator temperatureRegulator = TemperatureRegulator.getInstance();

       int result = temperatureRegulator.adjustTemp(enableInsert, temperature.getTemperature(), new ArrayList<>(), 0);
       if (result != 0) {
           log.error("Код ошибки: {}", result);
       }

       TemperatureRegulator.removeInstance();
    }

    @Operation(summary = "Получить текущее значение температуры")
    @GetMapping("current")
    public TemperatureDto getCurrent() {
        // Значение для включения 3-ого бита чтобы получить список значений.
        // Но не указываем далее количество значений, которое нужно получить. Нужно получить последнее актуальное значение
        byte enabledToGetList = 33;

        List<Float> temperatures = new ArrayList<>();

        Regulator temperatureRegulator = TemperatureRegulator.getInstance();

        temperatureRegulator.adjustTemp(enabledToGetList, 0, temperatures, -1);

        TemperatureRegulator.removeInstance();

        return new TemperatureDto(temperatures.getLast());
    }

    @Operation(summary = "Получить список значений температуры")
    @GetMapping
    public List<TemperatureDto> getTemperatures(
            @RequestParam(name = "offsetOut") int offsetOut,
            @RequestParam(name = "count") int count) {
        List<Float> temperatures = new ArrayList<>();
        Regulator temperatureRegulator = TemperatureRegulator.getInstance();

        // Значение для включения 3-ого бита чтобы получить список значений
        byte enabledToGetList = 33;

        // count смещаем на один разряд вперед, чтобы значение count заняло в двоичном виде с 4-ого по 7-ой бит
        byte operation = (byte) (enabledToGetList | count << 1);

        temperatureRegulator.adjustTemp(operation, 0, temperatures, 0);
        TemperatureRegulator.removeInstance();

        return temperatures.stream().map(TemperatureDto::new).collect(Collectors.toList());
    }
}
