package com.ghufran.microservices.limitsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghufran.microservices.limitsservice.bean.LimitConfiguration;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class LimitsConfigurationController {
	
	@Autowired
	private Configuration configuration;
	
	@GetMapping("/limits")
	public LimitConfiguration retriveLimitsFromConfiguration() {
		return new LimitConfiguration(configuration.getMaximum(), configuration.getMinimum());
	}
	
	@GetMapping("/limits-fault-tolerance")
	@HystrixCommand(fallbackMethod="fallbackRetriveLimits")
	public LimitConfiguration retriveLimits() {
//		return new LimitConfiguration(configuration.getMaximum(), configuration.getMinimum());
		throw new RuntimeException("Test");
	}
	
	public LimitConfiguration fallbackRetriveLimits() {
		return new LimitConfiguration(9999, 9);
	}

}
