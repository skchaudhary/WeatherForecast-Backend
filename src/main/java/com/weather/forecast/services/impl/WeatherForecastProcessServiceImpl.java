package com.weather.forecast.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.model.AtmosphericPressure;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.Temperature;
import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.forecast.WeatherForecast;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeather;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeatherData;
import com.github.prominence.openweathermap.api.model.onecall.historical.HourlyHistorical;
import com.github.prominence.openweathermap.api.model.weather.Location;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import com.weather.forecast.models.WeatherLocation;
import com.weather.forecast.models.WeatherModel;
import com.weather.forecast.models.WeatherType;
import com.weather.forecast.services.KafkaProducerService;
import com.weather.forecast.services.WeatherForecastProcessService;
import com.weather.forecast.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WeatherForecastProcessServiceImpl implements WeatherForecastProcessService {

    @Value("${weather.api.token}")
    private String apiKey;

    @Value("${app.kafka.producer.topic-current}")
    private String currentDataTopic;

    @Value("${app.kafka.producer.topic-past}")
    private String pastDataTopic;

    @Value("${app.kafka.producer.topic-future}")
    private String futureDataTopic;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    OpenWeatherMapClient openWeatherClient;

    @Override
    public Optional<Weather> getCurrentWeatherForecast(String city) {
        openWeatherClient = new OpenWeatherMapClient(apiKey);
        CompletableFuture<Weather> completableFuture = openWeatherClient.currentWeather()
                .single()
                .byCityName(city)
                .language(Language.ENGLISH)
                .retrieveAsync()
                .asJava();
        Optional<Weather> weatherData = Optional.empty();
        try {
            Weather weather = completableFuture.get();
            WeatherModel weatherModel = getPopulatedWeatherData(weather);
            kafkaProducerService.produce(objectMapper.writeValueAsString(weatherModel), currentDataTopic);
            return Optional.of(weather);
        } catch (InterruptedException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error during parsing the weather data: {}", e.getMessage());
        }


        return weatherData;
    }

    private WeatherModel getPopulatedWeatherData(Weather weather) {
        WeatherModel weatherModel = new WeatherModel();
        weatherModel.setWeatherCondition(weather.getWeatherState().getWeatherConditionEnum());

        weatherModel.setHumidity(weather.getHumidity().getValue());
        weatherModel.setHumidityUnit(weather.getHumidity().getUnit());
        //Pressure
        AtmosphericPressure atmosphericPressure = weather.getAtmosphericPressure();
        weatherModel.setPhValue(atmosphericPressure.getValue());
        weatherModel.setSeaLevelValue(atmosphericPressure.getSeaLevelValue());
        weatherModel.setGroundLevelValue(atmosphericPressure.getGroundLevelValue());
        weatherModel.setPhValueUnit(atmosphericPressure.getUnit());

        // temperature
        Temperature temperature = weather.getTemperature();
        weatherModel.setMaxTemperature(temperature.getMaxTemperature());
        weatherModel.setMinTemperature(temperature.getMinTemperature());
        weatherModel.setFeelsLike(temperature.getFeelsLike());
        weatherModel.setTempUnit(temperature.getUnit());
        // change location to timestamp and set it
        WeatherLocation location = getWeatherLocation(weather.getLocation());
        weatherModel.setLocation(location);
        weatherModel.setCalculationTime(Timestamp.valueOf(weather.getCalculationTime()));

        String key = location.getCoordinate() + DateUtil.format(DateUtil.YMD_FORMAT, weatherModel.getCalculationTime());
        weatherModel.setKey(key);
        weatherModel.setType(WeatherType.CURRENT);

        return weatherModel;
    }

    private WeatherLocation getWeatherLocation(Location location) {
        WeatherLocation weatherLocation = new WeatherLocation();
        weatherLocation.setCoordinate(location.getCoordinate());
        weatherLocation.setName(location.getName());
        weatherLocation.setCountryCode(location.getCountryCode());
        weatherLocation.setSunriseTime(Timestamp.valueOf(location.getSunriseTime()));
        weatherLocation.setSunsetTime(Timestamp.valueOf(location.getSunsetTime()));
        return weatherLocation;
    }

    @Override
    public Optional<HistoricalWeatherData> getPastWeatherForecast(Double latitude, Double longitude) {
        openWeatherClient = new OpenWeatherMapClient(apiKey);
        CompletableFuture<HistoricalWeatherData> completableFuture = openWeatherClient.oneCall()
                .historical()
                .byCoordinateAndTimestamp(Coordinate.of(latitude, longitude), LocalDateTime.now().minusDays(5).toEpochSecond(ZoneOffset.UTC))
                .language(Language.ENGLISH)
                .retrieveAsync()
                .asJava();
        Optional<HistoricalWeatherData> historicalWeatherData = Optional.empty();
        try {
            HistoricalWeatherData weatherData = completableFuture.get();
            WeatherModel weatherModel = getPopulatedWeatherData(weatherData);
            kafkaProducerService.produce(objectMapper.writeValueAsString(weatherModel), pastDataTopic);
            return Optional.of(weatherData);
        } catch (InterruptedException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error during parsing the weather data: {}", e.getMessage());
        }
        return historicalWeatherData;
    }

    private WeatherModel getPopulatedWeatherData(HistoricalWeatherData weather) {
        WeatherModel weatherModel = new WeatherModel();
        HistoricalWeather historicalWeather = weather.getHistoricalWeather();

        weatherModel.setWeatherCondition(historicalWeather.getWeatherState().getWeatherConditionEnum());

        weatherModel.setHumidity(historicalWeather.getHumidity().getValue());
        weatherModel.setHumidityUnit(historicalWeather.getHumidity().getUnit());
        //Pressure
        com.github.prominence.openweathermap.api.model.onecall.AtmosphericPressure atmosphericPressure = historicalWeather.getAtmosphericPressure();
        weatherModel.setSeaLevelValue(atmosphericPressure.getSeaLevelValue());
        weatherModel.setPhValueUnit(atmosphericPressure.getUnit());

        // temperature
        populateTemperature(weatherModel, historicalWeather.getTemperature());

        weatherModel.setCalculationTime(Timestamp.valueOf(historicalWeather.getForecastTime()));

        List<HourlyHistorical> hourlyHistoricalList = weather.getHourlyList();
        List<WeatherModel> weatherModelList = new ArrayList<>();
        for (HourlyHistorical hourlyHistorical : hourlyHistoricalList) {
            WeatherModel weatherModel1 = new WeatherModel();
            weatherModel1.setWeatherCondition(hourlyHistorical.getWeatherState().getWeatherConditionEnum());

            weatherModel1.setHumidity(hourlyHistorical.getHumidity().getValue());
            weatherModel1.setHumidityUnit(hourlyHistorical.getHumidity().getUnit());
            //Pressure
            com.github.prominence.openweathermap.api.model.onecall.AtmosphericPressure atmosphericPressure1 = hourlyHistorical.getAtmosphericPressure();
            weatherModel1.setSeaLevelValue(atmosphericPressure.getSeaLevelValue());
            weatherModel1.setPhValueUnit(atmosphericPressure.getUnit());
            // temperature
            populateTemperature(weatherModel, hourlyHistorical.getTemperature());
            weatherModelList.add(weatherModel1);
        }
        weatherModel.setWeatherModelList(weatherModelList);

        String key = weather.getCoordinate() + DateUtil.format(DateUtil.YMD_FORMAT, weatherModel.getCalculationTime());
        weatherModel.setKey(key);
        weatherModel.setType(WeatherType.PAST);

        return weatherModel;
    }

    private void populateTemperature(WeatherModel weatherModel, com.github.prominence.openweathermap.api.model.onecall.Temperature temperature) {
        weatherModel.setTempValue(temperature.getValue());
        weatherModel.setFeelsLike(temperature.getFeelsLike());
        weatherModel.setTempUnit(temperature.getUnit());
    }

    @Override
    public Optional<Forecast> getFutureWeatherForecast(String city) {
        openWeatherClient = new OpenWeatherMapClient(apiKey);
        Forecast forecast = openWeatherClient.forecast5Day3HourStep()
                .byCityName(city)
                .count(15)
                .language(Language.ENGLISH)
                .retrieve()
                .asJava();
        Optional<Forecast> futureForecastedData = Optional.empty();
        if (forecast != null) {
            try {
                WeatherModel weatherModel = getPopulatedWeatherData(forecast);
                kafkaProducerService.produce(objectMapper.writeValueAsString(weatherModel), futureDataTopic);
                return Optional.of(forecast);
            } catch (JsonProcessingException e) {
                log.error("Error during parsing the weather data: {}", e.getMessage());
            }
        }
        return futureForecastedData;
    }

    private WeatherModel getPopulatedWeatherData(Forecast forecast) {
        WeatherModel weatherModel = new WeatherModel();
        WeatherLocation location = getWeatherLocation(forecast.getLocation());
        weatherModel.setLocation(location);

        weatherModel.setCalculationTime(Timestamp.valueOf(LocalDateTime.now()));

        String key = location.getCoordinate() + DateUtil.format(DateUtil.YMD_FORMAT, weatherModel.getCalculationTime());
        weatherModel.setKey(key);
        weatherModel.setType(WeatherType.FUTURE);

        List<WeatherForecast> weatherForecasts = forecast.getWeatherForecasts();
        List<WeatherModel> weatherModelList = new ArrayList<>();
        for (WeatherForecast weatherForecast : weatherForecasts) {
            WeatherModel weatherModel1 = new WeatherModel();
            weatherModel1.setWeatherCondition(weatherForecast.getWeatherState().getWeatherConditionEnum());

            weatherModel1.setHumidity(weatherForecast.getHumidity().getValue());
            weatherModel1.setHumidityUnit(weatherForecast.getHumidity().getUnit());
            //Pressure
            AtmosphericPressure atmosphericPressure = weatherForecast.getAtmosphericPressure();
            weatherModel1.setPhValue(atmosphericPressure.getValue());
            weatherModel1.setSeaLevelValue(atmosphericPressure.getSeaLevelValue());
            weatherModel1.setGroundLevelValue(atmosphericPressure.getGroundLevelValue());
            weatherModel1.setPhValueUnit(atmosphericPressure.getUnit());

            // temperature
            Temperature temperature = weatherForecast.getTemperature();
            weatherModel1.setMaxTemperature(temperature.getMaxTemperature());
            weatherModel1.setMinTemperature(temperature.getMinTemperature());
            weatherModel1.setFeelsLike(temperature.getFeelsLike());
            weatherModel1.setTempUnit(temperature.getUnit());

            weatherModelList.add(weatherModel1);
        }
        weatherModel.setWeatherModelList(weatherModelList);
        return weatherModel;
    }

    private WeatherLocation getWeatherLocation(com.github.prominence.openweathermap.api.model.forecast.Location location) {
        WeatherLocation weatherLocation = new WeatherLocation();
        weatherLocation.setCoordinate(location.getCoordinate());
        weatherLocation.setName(location.getName());
        weatherLocation.setCountryCode(location.getCountryCode());
        weatherLocation.setSunriseTime(Timestamp.valueOf(location.getSunriseTime()));
        weatherLocation.setSunsetTime(Timestamp.valueOf(location.getSunsetTime()));
        return weatherLocation;
    }
}
