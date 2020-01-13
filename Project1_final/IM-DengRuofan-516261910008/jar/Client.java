import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.net.*;
public class Client
{
	private String name;
	private Channel channel;
	private UdpChannel udpChannel;
	private String talkingName;
	private int talkingPort;
	private DatagramSocket udpSocket;
	private int UdpPort;
	private OnSocketListener onSocketListener;
	
	public Client(String name, OnSocketListener onSocketListener) 
	{
		this.name = name;
		this.onSocketListener = onSocketListener;
		this.talkingPort = 0;
	}
	
	public void connect(String ip, int port, int UdpPort) throws IOException
	{
		this.UdpPort = UdpPort;
		Socket socket = new Socket(ip, port);
		udpSocket = new DatagramSocket(UdpPort);
		channel = new Channel(socket, onSocketListener);
		channel.start();
		udpChannel = new UdpChannel(udpSocket);
		udpChannel.start();
	}
	
	public void setTalking(String name, int port)
	{
		this.talkingName = name;
		this.talkingPort = port;
	}
	
	public int getTalkingPort()
	{
		return this.talkingPort;
	}
	
	public String getTalkingName()
	{
		return this.talkingName;
	}

	public void stop() throws IOException
	{
		channel.stop();
		udpChannel.stop();
	}
	
	public void send(String msg)
	{
		channel.send(name + " >> " + msg);
	}

	public void say(String msg) throws IOException
	{
		DatagramSocket s = new DatagramSocket();
		byte[] sendData = new byte[1024];
      	sendData = (name + " >> " + msg).getBytes();
      	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), talkingPort);
      	s.send(sendPacket);
	}
}
