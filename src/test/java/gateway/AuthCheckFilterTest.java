package gateway;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class AuthCheckFilterTest {

	@Test
	public void test1() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=1");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test2() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=2");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test3() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=3");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test4() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=3");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test5() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=5");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test6() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=6");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test7() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=7");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test8() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8090/bong", "test=8");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}
}