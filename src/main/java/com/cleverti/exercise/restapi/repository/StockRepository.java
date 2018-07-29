package com.cleverti.exercise.restapi.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cleverti.exercise.restapi.model.StockData;

public interface StockRepository extends CrudRepository<StockData, String> {

		List<StockData> findAll();
		
		List<StockData> findByDateBetweenAndCompany(Instant start, Instant end,String company);

		List<StockData> findByCompany(String company);
}
