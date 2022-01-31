package guru.springframework.brewery.web.controllers;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerT {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	CustomerRepository customerRepository;
	
	Customer customer;
	
	@BeforeEach
	void setUp() throws Exception {
		customer = customerRepository.findAll().get(0);
	}

	@Test
	void test() {
		String url = "/api/v1/customers/" + customer.getId().toString() + "/orders";
		BeerOrderPagedList pagedList = testRestTemplate.getForObject(url, BeerOrderPagedList.class);
		assertThat(pagedList.getContent()).hasSize(1);
	}

}
