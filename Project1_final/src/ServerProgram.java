import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.*;

public class ServerProgram implements OnSocketListener {
	private Server server;

	@Override
	public void onConnected(Channel channel) {
		Socket socket = channel.getSocket();
		String hostName = socket.getInetAddress().getHostName();
		int port = socket.getPort();

		String msg = "Client connected from " + hostName + ":" + port;

		System.out.println(msg);
	}

	@Override
	public void onDisconnected(Channel channel) {
		if (channel.getName() != null) {
			server.removeUser(channel.getName());
		}
		
		server.remove(channel);

		Socket socket = channel.getSocket();
		String hostName = socket.getInetAddress().getHostName();
		int port = socket.getPort();

		String msg = "Client disconnected from " + hostName + ":" + port;

		System.out.println(msg);
	}

	@Override
	public void onReceived(Channel channel, String msg) {
		System.out.println(msg);
		String[] command = msg.split(" ");
		
		if (command[2].equals("/add")) {
			
			// add user and add channel
			// to do
			String name = command[3];
			String port = command[4];
			server.addUser(name, port);
			server.addChannel(name, channel);
			//
			
			channel.setName(name);
		} 
		
		else if (command[2].equals("/connect")) {
			// the client wants to connect another client
			// to do
			String tname = command[3];
			String tport = server.getUser(tname);
			String talking_name = channel.getName();
			Channel talking_channel = server.getChannel(tname);
			
			channel.send("server >> /TargetPort " + tname + " " + tport);
			talking_channel.send("server >> /TargetPort " + talking_name + " " + server.getUser(talking_name));
			//
		} 
		
		else if (command[2].equals("/quit")) {
			String targetName = command[3];
			Channel targetChannel = server.getChannel(targetName);
			targetChannel.send("server >> /quit");
		}
		
	}

	public void start() throws IOException {
		Scanner scanner = new Scanner(System.in);
		int port = 20000;

		server = new Server(this);
		server.bind(port);
		server.start();

		while (true) {
			String msg = scanner.nextLine();

			if (msg.isEmpty())
				break;

			msg = "Server >> " + msg;

			System.out.println(msg);
			server.broadcast(msg);
		}

		scanner.close();
		server.stop();
	}

	public static void main(String[] args) throws IOException {
		ServerProgram program = new ServerProgram();
		program.start();
	}
}
