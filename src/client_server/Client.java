package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;

public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;

	private static ArrayList<String> ruka = new ArrayList<>();
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

	public void posaljiPorukuServeru(String string) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					printWriter.println(string);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	public void setSpremni(Runnable callback) {
		spremniPodaci = callback;
	}

	public void fromServer(String s) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					printWriter.println(s);
					int n = 1;
					if (s.equals("2")) {
						n = 5;
					}

					for (int i = 0; i < n; i++) {
						String line = bufferedReader.readLine();
						ruka.add(line);
					}

					n = 1;

					Platform.runLater(spremniPodaci);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static ArrayList<String> getRuka() {
		return ruka;
	}
}
