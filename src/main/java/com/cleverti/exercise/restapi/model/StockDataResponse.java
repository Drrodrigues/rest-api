package com.cleverti.exercise.restapi.model;

import java.util.LinkedList;
import java.util.List;

public class StockDataResponse {

	private Company company;
	
	private List<StockData> prices = new LinkedList<>();

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<StockData> getPrices() {
		return prices;
	}

	public void setPrices(List<StockData> prices) {
		this.prices = prices;
	}

	@Override
	public String toString() {
		return "StockDataResponse [company=" + company + ", prices=" + prices + "]";
	}
	
}
