package com.cleverti.exercise.restapi.model;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Company {

	@Id
	private String symbol;

	@JsonProperty("companyName")
	private String name;

	private String logo;

	public Company() {}
	
	public Company(String symbol, String name, String logo) {
		this.symbol = symbol;
		this.name = name;
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public String toString() {
		return "Company [symbol=" + symbol + ", name=" + name + ", logo=" + logo + "]";
	}

}
