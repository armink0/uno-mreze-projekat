package client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public final static int DEFAULT_PORT = 80;
	public final static String LOCALHOST = "localhost";
	
	private Socket socket;
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		Server server = new Server();
		server.execute();
	}

	void execute() {
		try {
			this.serverSocket = new ServerSocket(DEFAULT_PORT);
			System.out.println("Slusanje na portu " + DEFAULT_PORT);
			while (true) {
				this.socket = serverSocket.accept();
				System.out.println("Povezan korisnik");

				UserThread user = new UserThread(this, socket);

				user.start();
			}
		} catch (IOException ex) {
			System.err.println("Server errored: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}
