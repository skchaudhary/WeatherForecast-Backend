package com.weather.forecast.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("/weather")
public class WeatherForecastController {

    @Value("${weather.current}")
    private String currentWeatherUrl;

    /**
     * forecast current day weather data
     *
     * @return
     */
    @GetMapping("/forecast/current/{cityName}")
    public String currentWeather(@PathVariable String cityName) throws IOException {
        currentWeatherUrl = String.format(currentWeatherUrl, cityName);
        URL url = new URL(currentWeatherUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return response.toString();
        } else {
            System.out.println("GET request not worked");
            return con.getResponseMessage();
        }
    }

    /**
     * forecast future weeks weather data
     *
     * @return
     */
    @GetMapping("/forecast/future")
    public String forecastFutureWeather() {
        return null;
    }

    /**
     * forecast past weeks weather data
     *
     * @return
     */
    @GetMapping("/forecast/past")
    public String forecastPastWeather() {
        return null;
    }
}
