import java.io.*;
import java.net.*;
import java.util.*;

public class UdpChannel implements Runnable {
	private boolean running;
	private DatagramSocket socket;

	public UdpChannel(DatagramSocket socket) {
		this.socket = socket;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() throws IOException {
		running = false;
		socket.close();
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try {
					socket.receive(receivePacket);
				} catch (Exception e) {

				}
				String msg = new String(receivePacket.getData());
				received(msg);
			} catch (NoSuchElementException e) {
				break;
			}
		}

		disconnected();
	}

	private void disconnected() {
		try {
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void received(String msg) {
		System.out.println(msg);
	}

}
