package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserThread extends Thread {
	private Server server;
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private int n = 0;

	UserThread(Server server, Socket socket) {
		try {
			this.socket = socket;
			this.server = server;

			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			n = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			String clientMessage;

			do {
				// Read message from user
				clientMessage = bufferedReader.readLine();
				if (clientMessage == null) {
					break;
				}
				
				printWriter.println(clientMessage + n);
				n++;
			} while (!clientMessage.equals("exit"));

		} catch (IOException ex) {
			System.out.println("Greska u UserThread: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			// Close socket
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
