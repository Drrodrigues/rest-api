package com.cleverti.exercise.restapi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cleverti.exercise.restapi.configuration.ApplicationConfiguration;
import com.cleverti.exercise.restapi.controller.StockController;
import com.cleverti.exercise.restapi.model.Company;
import com.cleverti.exercise.restapi.model.StockData;
import com.cleverti.exercise.restapi.repository.CompanyRepository;
import com.cleverti.exercise.restapi.repository.StockRepository;

@Service
public class FetchDataService {

	protected static final String STOCK_URI = "https://api.iextrading.com/1.0/stock/%s";
	protected static final String COMPANY_INFO = STOCK_URI + "/company";
	protected static final String COMPANY_LOGO = STOCK_URI + "/logo";
	protected static final String COMPANY_PRICE = STOCK_URI + "/price";

	@Autowired
	ApplicationConfiguration configuration;

	@Autowired
	private RestTemplate httpRequest;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private StockRepository stockRepository;
	
	ScheduledFuture currentlyRunningThread;
	
	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(StockController.class);


	public void startFetchingService() {
		Runnable dataFetcher = ()-> { getDataFromEndpoints(); };
		int seconds = configuration.getSeconds();
		LOG.info("Scheduled thread to periodic fetch - every {} seconds ", seconds);

		currentlyRunningThread = Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(dataFetcher, 0, seconds, TimeUnit.SECONDS);
	}
	
	public void stopFetchingService() {
		currentlyRunningThread.cancel(true);
	}

	protected void getDataFromEndpoints() {
		
		configuration.getCompanies().stream().forEach(
			company -> {
				try {
					LOG.debug("Trying to obtain logo for company {}", company);
					String logo = httpRequest.getForObject(new URI(String.format(COMPANY_LOGO, company)), String.class);
					LOG.debug("Logo OK for company {}", company);
					
					LOG.debug("Trying to obtain company info for {}", company);
					Company companyData = httpRequest.getForObject(new URI(String.format(COMPANY_INFO, company)), Company.class);
					companyData.setLogo(logo);
					LOG.debug("Info OK for company {}", company);
					
					LOG.debug("Trying to obtain company price for {}", company);

					String price = httpRequest.getForObject(new URI(String.format(COMPANY_PRICE, company)), String.class);
					StockData data = new StockData();
					LOG.debug("Price OK for company {}", company);

					data.setDate(Instant.now());
					data.setPrice(Double.valueOf(price));
					data.setCompany(company);
					
					companyRepository.save(companyData);
					stockRepository.save(data);
					LOG.info("Finished all info OK for company {}", company);

					
				} catch (RestClientException | URISyntaxException exception) {
					LOG.error("There was a problem with the api / uri: " + exception.getLocalizedMessage());
				}
		});
	}
}
