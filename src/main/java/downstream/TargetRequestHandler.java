package downstream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetRequestHandler implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(TargetRequestHandler.class);
	private Socket connection;

	public TargetRequestHandler(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		log.debug("New client! Connected IP : {}, port : {}", connection.getInetAddress(), connection.getPort());
		OutputStream out = null;
		InputStream in = null;
		try {
			in = connection.getInputStream();
			out = connection.getOutputStream();

			// request
			DataInputStream dis = new DataInputStream(in);
			byte[] byteArr = new byte[25000];
			int readByteCount = dis.read(byteArr);
			log.info("readByteCount : {}", readByteCount);
//			String data = new String(byteArr, 0, readByteCount, "UTF-8");
			String data = "Hello I am Target";

			// response
			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = data.getBytes();
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}

				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
