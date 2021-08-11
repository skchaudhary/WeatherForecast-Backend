package com.weather.forecast.models.jwt;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String token;
    private final String user;
    public JwtResponse(String jwttoken, String user) {
        this.token = jwttoken;
        this.user = user;
    }
    public String getToken() {
        return this.token;
    }

    public String getUser() {
        return this.user;
    }
}