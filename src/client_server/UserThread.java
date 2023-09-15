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
			Server.setBroj(Server.getBroj() + 1);

			do {
				// Read message from user
				clientMessage = bufferedReader.readLine();

				if (clientMessage == null) {
					break;
				}

				if (clientMessage.equals("-2")) {
					printWriter.println(Server.getBroj());
				} else if (clientMessage.equals("-1")) {
					printWriter.println(Server.getGotovo());
				} else if (clientMessage.equals("0")) {
					printWriter.println(Server.getTrenutnaKarta());
				} else if (clientMessage.equals("1") || clientMessage.equals("2")) {
					if (Server.getKartaListaSize() > 0) {
						String karta = Server.getZadnjaKarta();
						printWriter.println(karta);
					}
				} else if (clientMessage.equals("3")) {
					int broj = Integer.parseInt(bufferedReader.readLine());
					printWriter.println(Server.getBroj());

					if (broj != Server.getBroj()) {
						String tk = bufferedReader.readLine();

						Server.setTrenutnaKarta(tk);

						clientMessage = bufferedReader.readLine();

						if (clientMessage.equals("gotovo")) {
							Server.setGotovo("gotovo");
						}

						printWriter.println(Server.getGotovo());

						broj = Integer.parseInt(bufferedReader.readLine());
						Server.setBroj(broj);
					}
				} else if (clientMessage.equals("4")) {
					printWriter.println(Server.getTrenutnaKarta());

					printWriter.println(Server.getBroj());
				} else if (clientMessage.equals("5")) {
					printWriter.println(Server.getBroj());

					int broj = Integer.parseInt(bufferedReader.readLine());
					Server.setBroj(broj);
				}
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

	public void sendMessage(String message) {
		// TODO Auto-generated method stub
		printWriter.println("poraz");
	}
}
