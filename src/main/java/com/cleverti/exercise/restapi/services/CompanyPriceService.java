package com.cleverti.exercise.restapi.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleverti.exercise.restapi.controller.StockController;
import com.cleverti.exercise.restapi.model.Company;
import com.cleverti.exercise.restapi.model.StockData;
import com.cleverti.exercise.restapi.model.StockDataResponse;
import com.cleverti.exercise.restapi.repository.CompanyRepository;
import com.cleverti.exercise.restapi.repository.StockRepository;

@Service
public class CompanyPriceService {

	@Autowired
	private StockRepository stockRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(StockController.class);


//this will be exclusive $gt and $lt
	public StockDataResponse getAllPricesForCompany(Instant start, Instant end, String symbol) {
		StockDataResponse stockData = new StockDataResponse();
		Optional<Company> company = companyRepository.findById(symbol);
		
		if(!company.isPresent()) {
			LOG.info("Company symbol was not present on DB - {}", company);
			return null;
		}
		List<StockData> prices;
		
		if(start!=null && end!=null) {
			prices = stockRepository.findByDateBetweenAndCompany(start, end, symbol);
		}
		else {
			prices = stockRepository.findByCompany(symbol);
		}
		stockData.setCompany(company.get());
		stockData.setPrices(prices);
		
		return stockData;
	}
	
}
