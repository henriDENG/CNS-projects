import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Server implements Runnable {
	private boolean running;
	private Map<String, String> users;

	private ServerSocket serverSocket;
	private ArrayList<Channel> channels;
	private Map<String, Channel> channelMap;

	private OnSocketListener onSocketListener;

	public Server(OnSocketListener onSocketListener) {
		this.onSocketListener = onSocketListener;
		this.users = new HashMap<String, String>();
		this.channelMap = new HashMap<String, Channel>();
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void bind(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() throws IOException {
		running = false;
		serverSocket.close();
	}

	public void addUser(String name, String port) {
		users.put(name, port);
	}

	public String getUser(String name) {
		return users.get(name);
	}

	public void removeUser(String name) {
		users.remove(name);
	}

	public void printUsers() {
		for (String key : users.keySet()) {
			System.out.println("Key = " + key);
			System.out.println("Value = " + users.get(key));
		}
	}

	public void addChannel(String name, Channel channel) {
		channelMap.put(name, channel);
	}

	public Channel getChannel(String name) {
		return channelMap.get(name);
	}

	@Override
	public void run() {
		channels = new ArrayList<>();

		running = true;
		while (running) {
			try {
				Socket socket = serverSocket.accept();

				Channel channel = new Channel(socket, onSocketListener);
				channel.start();

				channels.add(channel);
			} catch (SocketException e) {
				break;
			} catch (IOException e) {
				break;
			}
		}

		try {
			for (Channel channel : channels) {
				channel.stop();
			}

			channels.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcast(String msg) {
		if (!running)
			return;

		for (Channel channel : channels) {
			channel.send(msg);
		}
	}

	public void remove(Channel channel) {
		if (!running)
			return;

		channels.remove(channel);
	}
}
