package client_server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
	public final static int DEFAULT_PORT = 80;
	public final static String LOCALHOST = "localhost";
	public final static int POCETNO = 7;

	private Socket socket;
	private ServerSocket serverSocket;

	private static ArrayList<UserThread> korisniciLista = new ArrayList<>();
	private static ArrayList<String> kartaLista = new ArrayList<>();
	private static String trenutnaKarta;

	private static String gotovo = "nastavi";
	private static int broj = 0;

	public static void main(String[] args) {
		Server server = new Server();
		server.execute();
	}

	public synchronized static void setTrenutnaKarta(String trenutnaKarta) {
		Server.trenutnaKarta = trenutnaKarta;
	}

	public synchronized static String getTrenutnaKarta() {
		return Server.trenutnaKarta;
	}

	public synchronized static String oduzmiKartaLista() {
		String n = null;

		if (kartaLista.size() > 0) {
			n = kartaLista.remove(kartaLista.size() - 1);
		}

		return n;
	}

	public synchronized static int getKartaListaSize() {
		return kartaLista.size();
	}

	public synchronized static ArrayList<String> getKartaLista() {
		return kartaLista;
	}

	public synchronized static String getZadnjaKarta() {
		return kartaLista.remove(kartaLista.size() - 1);
	}

	public synchronized static String getGotovo() {
		return gotovo;
	}

	public synchronized static void setGotovo(String gotovo) {
		Server.gotovo = gotovo;
	}

	public static void broadcast(UserThread sender, String message) {
		synchronized (korisniciLista) {
			korisniciLista.stream().filter(u -> u != sender).forEach(u -> u.sendMessage(message));
		}
	}

	public synchronized static int getBroj() {
		return broj;
	}

	public synchronized static void setBroj(int broj) {
		Server.broj = broj;
	}

	void execute() {
		try {
			this.serverSocket = new ServerSocket(DEFAULT_PORT);
			System.out.println("Slusanje na portu " + DEFAULT_PORT);

			for (int i = 0; i < 4; i++) {
				String boja = null;

				if (i == 0) {
					boja = "zelena";
				} else if (i == 1) {
					boja = "plava";
				} else if (i == 2) {
					boja = "zuta";
				} else if (i == 3) {
					boja = "crvena";
				}

				for (int j = 1; j <= 9; j++) {
					kartaLista.add(j + ", " + boja);
				}
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
