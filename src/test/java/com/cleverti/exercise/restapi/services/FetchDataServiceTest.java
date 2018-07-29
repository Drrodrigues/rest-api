package com.cleverti.exercise.restapi.services;

import static org.mockito.Mockito.times;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cleverti.exercise.restapi.configuration.ApplicationConfiguration;
import com.cleverti.exercise.restapi.model.Company;
import com.cleverti.exercise.restapi.repository.CompanyRepository;
import com.cleverti.exercise.restapi.repository.StockRepository;
import com.cleverti.exercise.restapi.services.FetchDataService;

@RunWith(MockitoJUnitRunner.class)
public class FetchDataServiceTest {

	private static final TestData COMPANY_1 = new TestData("A", "Benfica", "www.slbenfica.pt", 15);
	private static final TestData COMPANY_2 = new TestData("S", "SLB", "www.sapo.pt", 36);
	private static final TestData COMPANY_3 = new TestData("C", "Champion", "www.someurl.pt", 2);

	@InjectMocks
	FetchDataService fetchService;

	@Mock
	private RestTemplate httpRequest;

	@Mock
	private ApplicationConfiguration configuration;

	@Mock
	private CompanyRepository companyRepository;

	@Mock
	private StockRepository stockRepository;

	@Test
	public void shouldCreateThreadAndDoRequestsAndThenStop() throws InterruptedException, RestClientException, URISyntaxException {
		setup();

		fetchService.startFetchingService();
		// let fetch service thread execute
		Thread.sleep(1000);

		Mockito.verify(companyRepository, times(3)).save(Mockito.any());
		Mockito.verify(stockRepository, times(3)).save(Mockito.any());
		Mockito.verify(configuration, times(1)).getCompanies();
		Mockito.verify(configuration, times(1)).getSeconds();
		Mockito.verify(httpRequest, times(9)).getForObject(Mockito.any(), Mockito.any());

		fetchService.stopFetchingService();
		Mockito.verifyNoMoreInteractions(configuration);
		Mockito.verifyNoMoreInteractions(stockRepository);
		Mockito.verifyNoMoreInteractions(companyRepository);
		Mockito.verifyNoMoreInteractions(httpRequest);

	}

	private void setup() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		initiateMocks(COMPANY_1);
		initiateMocks(COMPANY_2);
		initiateMocks(COMPANY_3);

		Mockito.when(configuration.getCompanies())
				.thenReturn(Arrays.asList(COMPANY_1.symbol, COMPANY_2.symbol, COMPANY_3.symbol));

		Mockito.when(configuration.getSeconds()).thenReturn(Integer.MAX_VALUE);
	}

	@Test
	public void shouldDoRequestsAndValidateData() throws InterruptedException, RestClientException, URISyntaxException {
		setup();
		List<Company> companies = new ArrayList<>(3);
		
		Mockito.when(companyRepository.save(Mockito.any())).then((invocation) -> {
			companies.add(invocation.getArgument(0));
			return invocation.getArgument(0);
		});
		
		fetchService.startFetchingService();
		// let fetch service thread execute
		Thread.sleep(1000);


		Assert.assertEquals(companies.size(), 3);
		Assert.assertEquals(companies.get(0).getLogo(), COMPANY_1.logo);
		Assert.assertEquals(companies.get(1).getLogo(), COMPANY_2.logo);
		Assert.assertEquals(companies.get(2).getLogo(), COMPANY_3.logo);

		Assert.assertEquals(companies.get(0).getName(), COMPANY_1.name);
		Assert.assertEquals(companies.get(1).getName(), COMPANY_2.name);
		Assert.assertEquals(companies.get(2).getName(), COMPANY_3.name);

		Assert.assertEquals(companies.get(0).getSymbol(), COMPANY_1.symbol);
		Assert.assertEquals(companies.get(1).getSymbol(), COMPANY_2.symbol);
		Assert.assertEquals(companies.get(2).getSymbol(), COMPANY_3.symbol);

		fetchService.stopFetchingService();
	}

	private void initiateMocks(TestData company) throws RestClientException, URISyntaxException {
		Mockito.when(httpRequest.getForObject(company.getLogoURI(), String.class)).thenReturn(company.logo);

		Mockito.when(httpRequest.getForObject(company.getInfoURI(), Company.class))
				.thenReturn(new Company(company.symbol, company.name, company.logo));

		Mockito.when(httpRequest.getForObject(company.getPriceURI(), String.class))
				.thenReturn(String.valueOf(company.price));
	}

	private static class TestData {
		private String symbol;
		private String name;
		private String logo;
		private double price;

		public TestData(String symbol, String name, String logo, double price) {
			this.symbol = symbol;
			this.name = name;
			this.logo = logo;
			this.price = price;
		}

		private URI getLogoURI() throws URISyntaxException {
			return new URI(String.format(FetchDataService.COMPANY_LOGO, symbol));
		}

		private URI getInfoURI() throws URISyntaxException {
			return new URI(String.format(FetchDataService.COMPANY_INFO, symbol));
		}

		private URI getPriceURI() throws URISyntaxException {
			return new URI(String.format(FetchDataService.COMPANY_PRICE, symbol));
		}
	}
}
