package com.cleverti.exercise.restapi.model;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StockData {

	@Id
	@JsonIgnore
	private String id;

	@Indexed
	private Instant date;

	private double price;

	@Indexed(name="companyIndex")
	@JsonIgnore
	private String company;

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
