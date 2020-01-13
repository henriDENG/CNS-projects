import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Channel implements Runnable {
	private boolean running;
	private Socket socket;
	private String name;

	private PrintWriter writer;
	private Scanner reader;

	private OnSocketListener onSocketListener;

	public Channel(Socket socket, OnSocketListener onSocketListener) {
		this.socket = socket;
		this.onSocketListener = onSocketListener;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() throws IOException {
		running = false;

		writer.close();
		reader.close();
		socket.close();
	}

	public void send(String msg) {
		writer.println(msg);
		writer.flush();
	}

	@Override
	public void run() {
		connected();

		running = true;
		while (running) {
			try {
				String msg = reader.nextLine();
				received(msg);
			} catch (NoSuchElementException e) {
				break;
			}
		}

		disconnected();
	}

	private void connected() {
		try {
			OutputStream outputStream = socket.getOutputStream();
			writer = new PrintWriter(outputStream);

			InputStream inputStream = socket.getInputStream();
			reader = new Scanner(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null != onSocketListener)
			onSocketListener.onConnected(this);
	}

	private void disconnected() {
		try {
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null != onSocketListener)
			onSocketListener.onDisconnected(this);
	}

	private void received(String msg) {
		if (null != onSocketListener)
			onSocketListener.onReceived(this, msg);
	}

}
