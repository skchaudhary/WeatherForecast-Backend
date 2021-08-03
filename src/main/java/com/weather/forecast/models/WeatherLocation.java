package com.weather.forecast.models;

import com.github.prominence.openweathermap.api.model.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class WeatherLocation {
    private int id;
    private String name;
    private String countryCode;
    private Date sunriseTime;
    private Date sunsetTime;
    private Coordinate coordinate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(Date sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public Date getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(Date sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof WeatherLocation)) {
            return false;
        } else {
            WeatherLocation location = (WeatherLocation)o;
            return this.id == location.id && Objects.equals(this.name, location.name) && Objects.equals(this.countryCode, location.countryCode) && Objects.equals(this.sunriseTime, location.sunriseTime) && Objects.equals(this.sunsetTime, location.sunsetTime) && Objects.equals(this.coordinate, location.coordinate);
        }
    }


    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.countryCode, this.sunriseTime, this.sunsetTime, this.coordinate});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.coordinate != null) {
            stringBuilder.append(this.coordinate.toString());
            stringBuilder.append(". ");
        }

        stringBuilder.append("ID: ");
        stringBuilder.append(this.id);
        stringBuilder.append(", Name: ");
        stringBuilder.append(this.name);
        if (this.countryCode != null) {
            stringBuilder.append('(');
            stringBuilder.append(this.countryCode);
            stringBuilder.append(')');
        }

        return stringBuilder.toString();
    }
}
