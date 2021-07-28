package com.weather.forecast.repository;

import com.weather.forecast.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByEmail(String email);
}
