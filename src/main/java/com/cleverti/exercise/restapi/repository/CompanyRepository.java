package com.cleverti.exercise.restapi.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cleverti.exercise.restapi.model.Company;

public interface CompanyRepository extends CrudRepository<Company, String> {

		List<Company> findAll();
		
		Company findByName(String name);

}
