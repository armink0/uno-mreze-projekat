package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserThread extends Thread {
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;

	private int n;

	UserThread(Socket socket) {
		try {
			this.socket = socket;

			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
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
<<<<<<< HEAD
				
				printWriter.println(clientMessage);
=======

				if (clientMessage.equals("1")) {
					n = Server.oduzmiKartaLista();
					printWriter.println(n + ", " + Server.getKartaListaSize());
				} else if (clientMessage.equals("2")) {
					for (int i = 0; i < 5; i++) {
						n = Server.oduzmiKartaLista();
						printWriter.println(n + ", " + Server.getKartaListaSize());
					}
				} else {
					n = Server.getKartaListaSize();
				}

>>>>>>> b614b23 (popravka servera i klijenta)
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
