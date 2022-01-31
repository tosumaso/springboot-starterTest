package guru.springframework.brewery.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;

//@WebMvcTest(テスト対象クラス): SpringMVCのテストをするために必要,MVC周りのBean(@Controller,WebMvcConfigurer,SpringSecurity,MockMvcなど)のみで構成される
//部分的にSpringContextを読み込んでおり、@Service,@Repository,@ComponentなどのBeanは使用できない
@WebMvcTest(BeerController.class)
class BeerControllerTest {

	//@MockBean: SpringBootの機能のMock化,Junitの@Mockと違いSpringContextに登録されたbeanをmock化して登録後、呼び出す
	@MockBean
	BeerService beerService;

//	@InjectMocks
//	BeerController beerController;

	//springbootでautoconfigureされたMockMvcのbeanを@Autowiredで呼び出す
	//MockMvcBuildersで手動でMVC環境を設定する必要がなくなった
	@Autowired
	MockMvc mockMvc;

	BeerDto validBeer;

	@BeforeEach
	void setUp() throws Exception {
		validBeer = BeerDto.builder().id(UUID.randomUUID())
				.version(1)
				.beerName("Beer1")
				.beerStyle(BeerStyleEnum.PALE_ALE)
				.price(new BigDecimal("12.99"))
				.quantityOnHand(4)
				.upc(123456789012L)
				.createdDate(OffsetDateTime.now())
				.lastModifiedDate(OffsetDateTime.now())
				.build();
//		mockMvc = MockMvcBuilders.standaloneSetup(beerController)
//					.setMessageConverters(jackson2HttpMessageConverter()).build();
//		System.out.println("Outer");
	}
	
	//各テストの終わりにmockitoがmockのリセットを行う。
	@AfterEach
	void tearDown() {
		reset(beerService);
	}

	@Test
	void testGetBeerById() throws Exception {
		//given
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		when(beerService.findBeerById(any())).thenReturn(validBeer);

		MvcResult result = mockMvc.perform(get("/api/v1/beer/" + validBeer.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
				.andExpect(jsonPath("$.beerName", is("Beer1")))
				.andExpect(jsonPath("$.createdDate" , is(dateTimeFormatter.format(validBeer.getCreatedDate()))))
				.andReturn();
		
		System.out.println(result.getResponse().getContentAsString());
	}

	@DisplayName("List Ops -")
	@Nested
	public class TestListOperations {

		@Captor
		ArgumentCaptor<String> beerNameCaptor;

		@Captor
		ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

		@Captor
		ArgumentCaptor<PageRequest> pageRequestCaptor;

		BeerPagedList beerPagedList;

		@BeforeEach
		void setUp() {
			List<BeerDto> beers = new ArrayList<BeerDto>();
			beers.add(validBeer);
			beers.add(BeerDto.builder().id(UUID.randomUUID())
					.version(1)
					.beerName("Beer4")
					.upc(123123123122L)
					.beerStyle(BeerStyleEnum.PALE_ALE)
					.price(new BigDecimal("12.99"))
					.quantityOnHand(66)
					.createdDate(OffsetDateTime.now())
					.lastModifiedDate(OffsetDateTime.now())
					.build());

			beerPagedList = new BeerPagedList(beers, PageRequest.of(1, 1), 2L);
			
			when(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumCaptor.capture(), pageRequestCaptor.capture())).thenReturn(beerPagedList);
			
			System.out.println("Inner");
		}
		
		@DisplayName("Test list bears - no parameters")
		@Test
		void testListBeers() throws Exception {
			mockMvc.perform(get("/api/v1/beer")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.content" , hasSize(2)))
				.andExpect(jsonPath("$.content[0].id" ,is(validBeer.getId().toString())));
		}
	}
	
//	//JsonとJavaオブジェクトの変換を行うJacksonの設定インスタンスを返すメソッド
//	public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
//		ObjectMapper objectMapper = new ObjectMapper();
//		//configure :objectmapperに登録されている設定のon,offを変更する
//		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//		objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
//		//setSerializationInclusion :シリアライズ対象の設定
//		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		//registerModule :新しい設定の追加
//		objectMapper.registerModule(new JavaTimeModule());
//		return new MappingJackson2HttpMessageConverter(objectMapper);
//	}

}
