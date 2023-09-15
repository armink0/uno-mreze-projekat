package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Client {
	private Socket socket;
	protected BufferedReader bufferedReader;
	protected PrintWriter printWriter;

	private static ArrayList<String> ruka = new ArrayList<>();
	public static String trenutnaKarta;

	private static String gotovo = "nastavi";
	public static String naPotezu = ", na potezu";

	private static int broj = 0;

	Client(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CompletableFuture<String> toServer(String s) {
		CompletableFuture<String> future = new CompletableFuture<>();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					printWriter.println(s);
					String line;

					if (s.equals("-2")) {
						line = bufferedReader.readLine();

						setBroj(Integer.parseInt(line));

						future.complete(line);
					} else if (s.equals("-1")) {
						line = bufferedReader.readLine();

						Client.setGotovo(line);

						future.complete(Client.getGotovo());
					} else if (s.equals("0")) {
						trenutnaKarta = bufferedReader.readLine();

						future.complete(trenutnaKarta);
					} else if (s.equals("1") || s.equals("2")) {
						line = bufferedReader.readLine();

						if (line != null) {
							future.complete(line);
						}
					} else if (s.equals("3")) {
						printWriter.println(getBroj());
						line = bufferedReader.readLine();

						if (getBroj() != Integer.parseInt(line)) {
							String trenutna = trenutnaKarta;
							printWriter.println(trenutna);

							if (getRuka().size() == 1) {
								printWriter.println("gotovo");
							} else {
								printWriter.println("nastavi");
							}

							Client.setGotovo(bufferedReader.readLine());

							printWriter.println(getBroj());

							naPotezu = ", na potezu";

						} else {
							naPotezu = "";
						}

						future.complete(trenutnaKarta);
					} else if (s.equals("4")) {
						line = bufferedReader.readLine();

						trenutnaKarta = line;

						line = bufferedReader.readLine();

						if (getBroj() != Integer.parseInt(line)) {
							naPotezu = ", na potezu";
						} else {
							naPotezu = "";
						}

						future.complete(trenutnaKarta);
					} else if (s.equals("5")) {
						printWriter.println(getBroj());
						line = bufferedReader.readLine();

						if (getBroj() != Integer.parseInt(line)) {
							naPotezu = "";
						} else {
							naPotezu = ", na potezu";
						}

						future.complete(trenutnaKarta);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		return future;
	}

	public synchronized static void updateRuka(String trenutnaKarta) {
		ruka.removeIf(e -> e.equals(trenutnaKarta));
	}

	public synchronized static void addRuka(String karta) {
		ruka.add(karta);
	}

	public synchronized static ArrayList<String> getRuka() {
		return ruka;
	}

	public synchronized static String getGotovo() {
		return gotovo;
	}

	public synchronized static void setGotovo(String gotovo) {
		Client.gotovo = gotovo;
	}

	public synchronized static int getBroj() {
		return broj;
	}

	public synchronized static void setBroj(int broj) {
		Client.broj = broj;
	}
}
