package gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MyApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

	@Bean
	public RouteLocator myRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/bong")
						.filters(f -> f.filter(authCheckFilter()))
						.uri("http://localhost:8000"))
				.build();
	}

	@Bean(initMethod = "setup")
	public AuthCheckFilter authCheckFilter() {
		return new AuthCheckFilter();
	}
}
