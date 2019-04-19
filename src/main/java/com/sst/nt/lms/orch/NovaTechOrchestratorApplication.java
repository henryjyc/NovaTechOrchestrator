package com.sst.nt.lms.orch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NovaTechOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovaTechOrchestratorApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() { return new RestTemplate();}
}
