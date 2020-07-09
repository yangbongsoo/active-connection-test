package authcheck;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthCheckServer {

	private final static Logger log = LoggerFactory.getLogger(AuthCheckServer.class);
	private final int port;

	public AuthCheckServer(int port) {
		this.port = port;
	}

	private static AtomicInteger count = new AtomicInteger(1);

	public static void main(String[] args) throws Exception {
		AuthCheckServer server = new AuthCheckServer(8001);
		server.start();
	}

	public void start() throws IOException {
		ServerSocket socket = new ServerSocket(port);
		log.debug("Server Initialized");

		Socket connection;
		ExecutorService executor = Executors.newFixedThreadPool(10);
		while ((connection = socket.accept()) != null) {
			connection.setKeepAlive(false);
			int current = count.addAndGet(1);
			log.debug("received request : {}", current);
			AuthCheckRequestHandler authCheckRequestHandler = new AuthCheckRequestHandler(connection);
			executor.submit(authCheckRequestHandler);
		}
	}
}
