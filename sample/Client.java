// A Java program for a Client
import java.io.*;
import java.net.*;

public class Client {
	// initialize socket and input output streams
	private Socket socket = null;
	private DataInputStream din = null;
	private DataOutputStream dout = null;
	private BufferedReader br = null;

	// constructor to put ip address and port
	public Client(String address, int port)
	{
		// establish a connection
		try {
			socket = new Socket(address, port);
			System.out.println("Connected");

			// takes input from terminal
			br = new BufferedReader(new InputStreamReader(System.in));

			// takes input from socket
			din = new DataInputStream(socket.getInputStream());

			// sends output to the socket
			dout = new DataOutputStream(socket.getOutputStream());
		}
		catch (UnknownHostException u) {
			System.out.println(u);
			return;
		}
		catch (IOException i) {
			System.out.println(i);
			return;
		}

		// string to read message from input
		String lineFromServer = "";
		Float lineToServer = 0.0f;

		// keep reading until "Over" is input
		while (!lineToServer.equals(1.1f)) {
			try {
				lineToServer = Float.parseFloat(br.readLine());
				dout.writeFloat(lineToServer);
				dout.flush();

				// lineFromServer = din.readUTF();
				// System.out.println(lineFromServer);

			}
			catch (IOException i) {
				System.out.println(i);
			}
		}

		// close the connection
		try {
			dout.close();
			socket.close();
		}
		catch (IOException i) {
			System.out.println(i);
		}
	}

	public static void main(String args[])
	{
		Client client = new Client("127.0.0.1", 5000);
	}
}

