package com.ghufran.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrenceyConversionController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment enviroment;
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/amount/{amount}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal amount) {
		
		Map<String, String> uriValriables = new HashMap<String, String>();
		uriValriables.put("from", from);
		uriValriables.put("to", to);
		
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversionBean.class, 
				uriValriables);
		CurrencyConversionBean response = responseEntity.getBody();
		
		return new CurrencyConversionBean(
				response.getId(),
				from, 
				to, 
				response.getConversionFactor(), 
				amount, 
				amount.multiply(response.getConversionFactor()), 
				response.getPort(), Integer.parseInt(enviroment.getProperty("local.server.port")));
	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/amount/{amount}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal amount) {
		
		CurrencyConversionBean response = proxy.retreiveExchangeValue(from, to);
		
		logger.info("{}", response);
		
		return new CurrencyConversionBean(
				response.getId(),
				from, 
				to, 
				response.getConversionFactor(), 
				amount, 
				amount.multiply(response.getConversionFactor()), 
				response.getPort(), Integer.parseInt(enviroment.getProperty("local.server.port")));
	}
	
}
