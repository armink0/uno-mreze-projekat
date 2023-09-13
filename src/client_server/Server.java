package client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
	public final static int DEFAULT_PORT = 80;
	public final static String LOCALHOST = "localhost";

	private Socket socket;
	private ServerSocket serverSocket;

	private static ArrayList<Integer> kartaLista = new ArrayList<>();

	public static void main(String[] args) {
		for (int i = 1; i <= 15; i++) {
			kartaLista.add(i);
		}

		Collections.shuffle(kartaLista);

		Server server = new Server();
		server.execute();
	}

	public static int oduzmiKartaLista() {
		int n = 0;
		if(kartaLista.size() > 0) {
			n = kartaLista.remove(kartaLista.size() - 1);
		}
		return n;
	}

	public static int getKartaListaSize() {
		return kartaLista.size();
	}

	void execute() {
		try {
			this.serverSocket = new ServerSocket(DEFAULT_PORT);
			System.out.println("Slusanje na portu " + DEFAULT_PORT);

			while (true) {
				this.socket = serverSocket.accept();
				System.out.println("Povezan korisnik");

				UserThread user = new UserThread(socket);

				user.start();
			}
		} catch (IOException ex) {
			System.err.println("Server errored: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}
