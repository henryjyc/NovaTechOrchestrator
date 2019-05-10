package com.sst.nt.lms.orch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sst.nt.lms.orch.util.RestTemplateResponseErrorHandler;

@SpringBootApplication
public class NovaTechOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovaTechOrchestratorApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
		return restTemplate;
	}

	/**
	 * Enable cross-origin requests.
	 *
	 * @return the CORS filter
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(final CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT",
						"DELETE", "HEAD");
			}
		};
	}
}
