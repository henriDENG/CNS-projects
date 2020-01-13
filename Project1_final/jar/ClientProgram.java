import java.io.IOException;
import java.util.Scanner;

public class ClientProgram implements OnSocketListener {
	private Client client;

	@Override
	public void onConnected(Channel channel) {
		System.out.println("Server Connected.");
	}

	@Override
	public void onDisconnected(Channel channel) {
		System.out.println("Disconnected.");
	}

	@Override
	public void onReceived(Channel channel, String msg) {
		System.out.println(msg);
		
		// get the command
		String[] command = msg.split(" ");
		
		if (command[2].equals("/NoUserFound")) {
			System.out.println("No User Found");
		} 
		
		else if (command[2].equals("/TargetPort")) {
			
			// set talking with the target User
			// to do
			System.out.println("The UDP port of " + command[3] + " is: " + command[4]);
			client.setTalking(command[3], Integer.parseInt(command[4]));
			//
			
		} 
		
		else if (command[2].equals("/quit")) {
			System.out.println("Quit talking with " + client.getTalkingName());
			client.setTalking(null, 0);
		} 
		
		// solve the problem of broadcasting twice
		/*
		else {
			System.out.println(msg);
		}
		*/
			
	}

	public void start() throws IOException {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Name : ");
		String name = scanner.nextLine();

		String ip = "127.0.0.1";
		int port = 20000;

		System.out.print("Local UDP Port : ");
		int UdpPort = Integer.parseInt(scanner.nextLine());

		// start a new client
		// to do
		client = new Client(name, this);
		//
		
		// let the program wait a little bit
		for (int i = 0; i < 999999; i++) {
			int k = i * (i + 1);
		}
		
		// connect to the server and detect exceptions
		// to do
		try { 
			client.connect(ip, port, UdpPort);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		//
		
		while (true) {
			// get the input string
			String msg = scanner.nextLine();

			if (msg.isEmpty())
				break;
			String[] command = msg.split(" ");
			
			if (command[0].equals("/quit")) {
				client.send("/quit " + client.getTalkingName());
				System.out.println("Quit talking with " + client.getTalkingName());
				client.setTalking(null, 0);
			} else if (msg.charAt(0) == '/') {
				client.send(msg);
			} else if (client.getTalkingPort() != 0) {
				// send messages
				// to do
				client.say(msg);
				System.out.println("Message is sent to the destination port");
				//
			}
		}

		scanner.close();
		client.stop();
	}

	public static void main(String[] args) throws IOException {
		ClientProgram program = new ClientProgram();
		program.start();
	}
}
