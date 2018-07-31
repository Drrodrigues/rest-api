package com.cleverti.exercise.restapi.controller;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import com.cleverti.exercise.restapi.model.StockDataResponse;
import com.cleverti.exercise.restapi.services.CompanyPriceService;
import com.cleverti.exercise.restapi.services.FetchDataService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;

@Controller
public class StockController {

	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(StockController.class);
	
	@Autowired
	private FetchDataService fetchService;

	@Autowired
	private CompanyPriceService companyService;

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Starts Polling process", notes = "Polling period is defined in the spring configuration yml file")
	public void start() throws RestClientException, URISyntaxException {
		LOG.debug("Fetching service was ordered to start");
		fetchService.startFetchingService();
	}

	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Stop Polling process", notes = "Polling is interrupted", response = Boolean.class)
	public Boolean stop() throws RestClientException, URISyntaxException {
		LOG.debug("Fetching service was ordered to stop");
		fetchService.stopFetchingService();
		return true;
	}

	@RequestMapping(value = "/stockData", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Get Company Price information", response = StockDataResponse.class)
	@ApiResponse(code= 200, message="List of Stock Data information")
	public List<StockDataResponse> getStockData(
			@ApiParam(value = "Date in epoch seconds", example="1532730639") @RequestParam(value="startDate", required = false) long startDate,
			@ApiParam(value = "Date in epoch seconds", example="1532730840") @RequestParam(value="endDate", required = false) long endDate,
			@ApiParam(value = "List of symbols") @RequestParam("companies") List<String> companies)
			throws RestClientException, URISyntaxException {
		LOG.debug("Get Stock Data Parameters companies:{} startDate {} and endDate {} ", companies, startDate, endDate);

		List<StockDataResponse> responses = new LinkedList<>();
		for (String company : companies) {
			LOG.debug("Fetching Company prices for company:{} startDate {} and endDate {} ", company, startDate, endDate);
			StockDataResponse companyPrices = companyService.getAllPricesForCompany(Instant.ofEpochSecond(startDate),
					Instant.ofEpochSecond(endDate), company);
			responses.add(companyPrices);
			LOG.debug("Fetching Company prices {} ", companyPrices);

		}
		return responses;
	}
}
