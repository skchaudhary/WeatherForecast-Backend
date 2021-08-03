package com.weather.forecast.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.prominence.openweathermap.api.enums.WeatherCondition;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "weather")
@NoArgsConstructor
@Data
public class WeatherModel {
    @Id
    @Field("_id")
    @JsonIgnore
    private String id;
    private String key;
    private WeatherType type;
    private Date calculationTime;
    private WeatherCondition weatherCondition;
    // temp
    private Double maxTemperature;
    private Double tempValue;
    private Double minTemperature;
    private Double feelsLike;
    private String tempUnit;

    // pressure
    private double phValue;
    private Double seaLevelValue;
    private Double groundLevelValue;
    private String phValueUnit;

    private Integer humidity;
    private String humidityUnit;

    private WeatherLocation location;

    private List<WeatherModel> weatherModelList;
}
