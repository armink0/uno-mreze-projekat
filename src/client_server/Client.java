package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;

public class Client {
	private Socket socket;
	protected BufferedReader bufferedReader;
	protected PrintWriter printWriter;

	private static ArrayList<String> ruka = new ArrayList<>();
	public static String trenutnaKarta;
	private Runnable spremniPodaci;

	Client(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.printWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fromServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String line;

					if ((line = bufferedReader.readLine()) != null) {
						trenutnaKarta = line;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	public CompletableFuture<String> toServer(String s) {
		CompletableFuture<String> future = new CompletableFuture<>();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					printWriter.println(s);

					if (s.equals("0")) {
						trenutnaKarta = bufferedReader.readLine();

						future.complete(trenutnaKarta);
					} else if (s.equals("1") || s.equals("2")) {
						String line = bufferedReader.readLine();

						if (line != null || !line.equals("0, 0")) {
							future.complete(line);
						}
					} else if (s.equals("3")) {
						printWriter.println(Integer.parseInt(trenutnaKarta.split(", ")[0]));

						future.complete(trenutnaKarta);
					} else if (s.equals("4")) {
						String line = bufferedReader.readLine();

						trenutnaKarta = line;
						future.complete(trenutnaKarta);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		return future;
	}

//	public static String getTrenutnaKarta() {
//		return trenutnaKarta;
//	}

//	public void setSpremni(Runnable callback) {
//		spremniPodaci = callback;
//	}
//
//	public static String getTrenutnaKarta() {
//		return trenutnaKarta;
//	}
//
	public static void updateRuka(String trenutnaKarta) {
		ruka.removeIf(e -> e.equals(trenutnaKarta));
	}

	public synchronized static void addRuka(String karta) {
		ruka.add(karta);
	}

	public static ArrayList<String> getRuka() {
		return ruka;
	}
}
