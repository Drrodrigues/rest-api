package com.cleverti.exercise.restapi.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Profile("!test")
@EnableSwagger2
@Component
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.cleverti.exercise.restapi")).paths(PathSelectors.any())
				.build();
	}

	@Bean
	public ApiInfo apiInfo() {
		return new ApiInfo("Cleverti Rest API", "Stock App", "API TOS", "Terms of service",
				new Contact("Diogo Rodrigues", "www.linkedin.com/in/jdiogorodrigues", "rodrigues.jdiogo@company.com"),
				"License of API", "API license URL", Collections.emptyList());
	}

}
