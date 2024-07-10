// A Java program for a Server
import java.net.*;
import java.io.*;

public class Server
{
	//initialize socket and input stream
	private Socket clientSocket = null;
	private ServerSocket serverSocket = null;
	private DataInputStream din	 = null;
	private DataOutputStream dout = null;
	private BufferedReader br = null;

	// constructor with port
	public Server(int port)
	{
		// starts server and waits for a connection
		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);

			System.out.println("Waiting for a client ...");

			clientSocket = serverSocket.accept();
			System.out.println("Client accepted " + clientSocket.getInetAddress());

			// takes input from the client socket
			din = new DataInputStream(clientSocket.getInputStream());
			
			dout = new DataOutputStream(clientSocket.getOutputStream());

			br = new BufferedReader(new InputStreamReader(System.in));

			String lineFromClient = "";
			String lineToClient = "";

			// reads message from client until "Over" is sent
			while (!lineFromClient.equals("Over"))
			{
				try
				{
					lineFromClient = Float.toString(din.readFloat());
					System.out.println(lineFromClient);

					lineToClient = br.readLine();
					dout.writeUTF(lineToClient);
					dout.flush();

				}
				catch(IOException i)
				{
					System.out.println(i);
					break;
				}
			}
			System.out.println("Closing connection");

			// close connection
			clientSocket.close();
			din.close();
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
	}

	public static void main(String args[])
	{
		Server server = new Server(5000);
	}
}

