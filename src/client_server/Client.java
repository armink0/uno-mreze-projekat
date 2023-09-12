package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;

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
		// new Thread(new Runnable() {
//			@Override
//			public void run() {
		try {
			printWriter.println(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//		}).start();
//	}

	public void vratiPorukuOdServera() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String line = bufferedReader.readLine();
					System.out.println(line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
