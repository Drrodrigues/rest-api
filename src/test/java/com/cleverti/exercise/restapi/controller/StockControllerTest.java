package com.cleverti.exercise.restapi.controller;

import static org.mockito.Mockito.times;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;

import com.cleverti.exercise.restapi.controller.StockController;
import com.cleverti.exercise.restapi.services.FetchDataService;

@RunWith(MockitoJUnitRunner.class)
public class StockControllerTest {

	@Mock
	private FetchDataService service;

	@InjectMocks
	private StockController controller;

	@Test
	public void shouldCallServiceStart() throws RestClientException, URISyntaxException {
		controller.start();
		Mockito.verify(service, times(1)).startFetchingService();
	}

	@Test
	public void shouldCallServiceStop() throws RestClientException, URISyntaxException {
		controller.stop();
		Mockito.verify(service, times(1)).stopFetchingService();
	}
}
