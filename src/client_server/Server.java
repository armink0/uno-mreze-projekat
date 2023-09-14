package client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
	public final static int DEFAULT_PORT = 80;
	public final static String LOCALHOST = "localhost";
	public final static int POCETNO = 3;

	private Socket socket;
	private ServerSocket serverSocket;

	private static ArrayList<UserThread> korisniciLista = new ArrayList<>();
	private static ArrayList<Integer> kartaLista = new ArrayList<>();
	private static int trenutnaKarta;

	public static void main(String[] args) {
		Server server = new Server();
		server.execute();
	}

	public synchronized static void setTrenutnaKarta(int trenutnaKarta) {
		Server.trenutnaKarta = trenutnaKarta;
	}

//	public static void broadcast(String poruka) {
//		for (UserThread ut : korisniciLista) {
//			ut.posaljiPoruku(poruka);
//		}
//	}

	public synchronized static int getTrenutnaKarta() {
		return Server.trenutnaKarta;
	}

	public synchronized static int oduzmiKartaLista() {
		int n = 0;

		if (kartaLista.size() > 0) {
			n = kartaLista.remove(kartaLista.size() - 1);
		}

		return n;
	}

	public synchronized static int getKartaListaSize() {
		return kartaLista.size();
	}

	public synchronized static ArrayList<Integer> getKartaLista() {
		return kartaLista;
	}

	public synchronized static int getZadnjaKarta() {
		return kartaLista.remove(kartaLista.size() - 1);
	}

	void execute() {
		try {
			this.serverSocket = new ServerSocket(DEFAULT_PORT);
			System.out.println("Slusanje na portu " + DEFAULT_PORT);

			for (int i = 1; i <= 10; i++) {
				kartaLista.add(i);
			}

			Collections.shuffle(kartaLista);

			trenutnaKarta = kartaLista.remove(kartaLista.size() - 1);

			while (true) {
				this.socket = serverSocket.accept();
				System.out.println("Povezan korisnik");

				UserThread user = new UserThread(socket);
				korisniciLista.add(user);

				user.start();
			}
		} catch (IOException ex) {
			System.err.println("Server errored: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
