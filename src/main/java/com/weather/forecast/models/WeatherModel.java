package com.weather.forecast.models;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weather")
@NoArgsConstructor
@Data
public class WeatherModel {
    @BsonIgnore
    @Id
    private Object id;
    private String key;
    private WeatherType type;
    private String data;

    public WeatherModel(WeatherType type, String key, String data) {
        this.type = type;
        this.key = key;
        this.data = data;
    }
}
