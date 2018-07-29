package com.cleverti.exercise.restapi.integrated;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.cleverti.exercise.restapi.configuration.SwaggerConfig;
import com.cleverti.exercise.restapi.model.Company;
import com.cleverti.exercise.restapi.model.StockData;
import com.cleverti.exercise.restapi.repository.CompanyRepository;
import com.cleverti.exercise.restapi.repository.StockRepository;

import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@DataMongoTest
@ComponentScan(basePackages = "com.cleverti.exercise.restapi",excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = {SwaggerConfig.class})})
public class FetchDataServiceIT {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private CompanyRepository companyRepository;

	Instant NOW = Instant.now();

	@Before
	public void setup() {
		Company company = new Company("A", "Apple", "random");
		companyRepository.save(company);
		for (int i = 0; i < 5; i++) {
			StockData data = new StockData();
			data.setCompany("A");
			data.setPrice(i * 100);
			data.setDate(NOW.plus(i, ChronoUnit.MINUTES));
			stockRepository.save(data);
		}
	}

	@Test
	public void ShouldGetBetweenDates() {
		List<StockData> data = stockRepository.findByDateBetweenAndCompany(NOW.minusSeconds(1), NOW.plus(3, ChronoUnit.MINUTES), "A");
		Assert.assertNotNull(data);
		Assert.assertEquals(data.size(), 3);
		Assert.assertEquals(data.get(0).getPrice(), 0,0);
		Assert.assertEquals(data.get(0).getDate(), NOW);
		Assert.assertEquals(data.get(1).getPrice(), 100,0);
		Assert.assertEquals(data.get(1).getDate(), NOW.plusSeconds(60));
		Assert.assertEquals(data.get(2).getPrice(), 200,0);
		Assert.assertEquals(data.get(2).getDate(), NOW.plusSeconds(120));

	}

	@Configuration
	static class ContextConfiguration {
		@Bean
		public RestTemplateBuilder restTemplateBuilder() {

			RestTemplateBuilder rtb = mock(RestTemplateBuilder.class);
			RestTemplate restTemplate = mock(RestTemplate.class);

			when(rtb.build()).thenReturn(restTemplate);
			return rtb;
		}

		@Bean
		public WebMvcRequestHandlerProvider swaggerMock() {
			return mock(WebMvcRequestHandlerProvider.class);
		}
	}

}
