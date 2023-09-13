package client_server;

import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ClientController extends Application {
	private Client client;

	@Override
	public void start(Stage primaryStage) {
		try {
			client = new Client(new Socket(Server.LOCALHOST, Server.DEFAULT_PORT));
			System.out.println("Uspjesna konekcija");
		} catch (Exception e) {
			e.printStackTrace();
		}

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(25));

		ListView<String> listView = new ListView<>();
		listView.setMaxSize(200, 200);

		Button vratiKarte = new Button("vrati karte u ruci");

		vratiKarte.setOnAction(e -> {
			listView.getItems().clear();

			synchronized (client) {
				client.fromServer("1");
			}

			client.setSpremni(() -> {
				ArrayList<String> list = new ArrayList<>();

				for (String s : Client.getRuka()) {
					list.add(s);
				}

				listView.getItems().addAll(list);
			});
		});
		
		Button popuniKarte = new Button("popuni karte u ruci");

		popuniKarte.setOnAction(e -> {
			listView.getItems().clear();

			synchronized (client) {
				client.fromServer("2");
			}

			client.setSpremni(() -> {
				ArrayList<String> list = new ArrayList<>();

				for (String s : Client.getRuka()) {
					list.add(s);
				}

				listView.getItems().addAll(list);
			});
			
			popuniKarte.setDisable(true);
		});

		root.getChildren().addAll(listView, vratiKarte, popuniKarte);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Klijent");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
