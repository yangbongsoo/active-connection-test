package gateway;

import static org.springframework.http.MediaType.*;
import static reactor.core.publisher.Mono.*;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import authcheck.AuthCheckFailException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;
import reactor.retry.Retry;

public class AuthCheckFilter implements GatewayFilter {

	private static final Logger logger = LoggerFactory.getLogger(AuthCheckFilter.class);
	private WebClient.Builder webClientBuilder;
	private StopWatch stopWatch;

	public void setup() {
		// ELASTIC
//		ConnectionProvider provider = ConnectionProvider.elastic("auth-check-pool", Duration.ofMillis(8000L));
		ConnectionProvider provider = ConnectionProvider.newConnection();
//		ConnectionProvider provider = ConnectionProvider.builder("auth-check-pool")
//				.maxConnections(Integer.MAX_VALUE)
//				.pendingAcquireTimeout(Duration.ofMillis(0))
//				.pendingAcquireMaxCount(-1)
//				.maxIdleTime(Duration.ofMillis(8000L))
//				.maxLifeTime(null)
//				.maxLifeTime(Duration.ofMillis(8000L))
//				.metrics(true)
//				.build();

		TcpClient tcpClient = TcpClient.create(provider)
				.port(80)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000)
				.metrics(true)
				.doOnConnected(
						c -> c.addHandlerLast(new ReadTimeoutHandler(5))
								.addHandlerLast(new WriteTimeoutHandler(5)));
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector(HttpClient.from(tcpClient).compress(true).wiretap(false));

		this.webClientBuilder = WebClient.builder()
				.clientConnector(connector)
				.baseUrl("http://localhost:8001");
		stopWatch = new StopWatch("ybs-stopwatch");
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		logger.info("[AuthCheckFilter] start");

		Mono<ResponseEntity<String>> authCheckResponse = makeAuthCheckApi(exchange, this.webClientBuilder);
		return authCheckResponse.then(chain.filter(exchange));
	}

	private Mono<ResponseEntity<String>> makeAuthCheckApi(ServerWebExchange exchange, WebClient.Builder webClientBuilder) {
		stopWatch.start();
		return webClientBuilder
				.build()
				.post()
				.uri(uriBuilder -> uriBuilder
						.path("/auth")
						.queryParam("txId","-" + System.currentTimeMillis())
						.build()
				)
				.contentType(APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromValue("auth-check-go-go"))
				.exchange()
				.flatMap(res -> res.toEntity(String.class))
				.doOnNext(result -> {
					if (result.getStatusCode() != HttpStatus.OK) {
						throw new AuthCheckFailException();
					}
				})
				.retryWhen(Retry.any().fixedBackoff(Duration.ofMillis(500)).retryMax(2)
						.doOnRetry((context) -> {
							logger.warn("AuthorityCheckApi doOnRetry : ", context.exception());
						}))
				.doOnSuccess( // 권한체크 api 서버와 네트워크 통신 성공
						result -> {
							stopWatch.stop();
							logger.info("stopWatch : {}", stopWatch.getLastTaskInfo().getTimeSeconds());
							if (result.getStatusCode() == HttpStatus.OK) {
								result.getBody();
							} else {

							}
						})
				.onErrorResume(error -> { // 물리적인 실패
					logger.error("error");
					return just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
				});
	}
}
