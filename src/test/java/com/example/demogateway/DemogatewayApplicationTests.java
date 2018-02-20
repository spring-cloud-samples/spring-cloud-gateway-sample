package com.example.demogateway;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemogatewayApplicationTests {

	@LocalServerPort
	int port;
	private WebTestClient client;

	@Before
	public void setup() {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void pathRouteWorks() {
		client.get().uri("/get")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Map.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody()).isNotEmpty();
				});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void hostRouteWorks() {
		client.get().uri("/headers")
				.header("Host", "www.myhost.org")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Map.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody()).isNotEmpty();
				});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void rewriteRouteWorks() {
		client.get().uri("/foo/get")
				.header("Host", "www.rewrite.org")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Map.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody()).isNotEmpty();
				});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void hystrixRouteWorks() {
		client.get().uri("/delay/3")
				.header("Host", "www.hystrix.org")
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void hystrixFallbackRouteWorks() {
		client.get().uri("/delay/3")
				.header("Host", "www.hystrixfallback.org")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("This is a fallback");
	}

	@Test
	public void rateLimiterWorks() {
		WebTestClient authClient = client.mutate()
				.filter(basicAuthentication("user", "password"))
				.build();

		boolean wasLimited = false;

		for (int i = 0; i < 20; i++) {
			FluxExchangeResult<Map> result = authClient.get()
					.uri("/anything/1")
					.header("Host", "www.limited.org")
					.exchange()
					.returnResult(Map.class);
			if (result.getStatus().equals(HttpStatus.TOO_MANY_REQUESTS)) {
				System.out.println("Received result: "+result);
				wasLimited = true;
				break;
			}
		}

		assertThat(wasLimited)
				.as("A HTTP 429 TOO_MANY_REQUESTS was not received")
				.isTrue();

	}

}
