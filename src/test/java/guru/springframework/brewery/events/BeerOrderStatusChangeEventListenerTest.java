package guru.springframework.brewery.events;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;

import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.domain.OrderStatusEnum;
//WireMockExtension: WireMockでサーバーをMockするために必要
@ExtendWith(WireMockExtension.class)
class BeerOrderStatusChangeEventListenerTest {

	//WireMockServerの設定。RandomPortの設定
	@Managed
	WireMockServer wireMockServer = with(wireMockConfig().dynamicPort());
	
	BeerOrderStatusChangeEventListener listener;
	
	@BeforeEach
	void setUp() throws Exception {
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		listener = new BeerOrderStatusChangeEventListener(restTemplateBuilder);
	}

	@Test
	void listen() {
		//WireMockServerにpostメソッド"/update"を送ったらstatus200(ok)が返るように設定
		wireMockServer.stubFor(post("/update").willReturn(ok()));
		
		BeerOrder beerOrder = BeerOrder.builder()
                .orderStatus(OrderStatusEnum.READY)
            .orderStatusCallbackUrl("http://localhost:" + wireMockServer.port() + "/update")
            .createdDate(Timestamp.valueOf(LocalDateTime.now()))
            .build();
		
		BeerOrderStatusChangeEvent event = new BeerOrderStatusChangeEvent(beerOrder,OrderStatusEnum.NEW);
		
		listener.listen(event);
		//WireMock.verify(times,requestedFor(urlPattern): WireMockServerがtimes回指定したurl宛てのrequestを受け取ることを証明
		verify(1,postRequestedFor(urlEqualTo("/update")));
	}

}
