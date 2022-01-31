package guru.springframework.brewery.web.controllers;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import guru.springframework.brewery.web.model.BeerPagedList;

//@SpringBootTest(webEnvironment): SpringContextを全て読み込んで結合テストを行う
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerControllerT {

	//全てのSpringContextを読み込んでいるためContextからTestRestTemplateのbeanをinjectできる
	//TestRestTemplate: 結合テスト用にHttpRequestを発信できる
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testListBeers() {
		//restTemplate.getForObject(url,returnedObject): テスト用にGetrequestを送り、指定した型のレスポンスのオブジェクトを取得
		BeerPagedList beerPagedList = restTemplate.getForObject("/api/v1/beer", BeerPagedList.class);
		assertThat(beerPagedList.getContent()).hasSize(3);
	}

}
