package client_server;

import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ClientController extends Application {
	private Client client;

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(25));

		Label prikazTrenutne = new Label("");

		ListView<String> listView = new ListView<>();
		listView.setMaxSize(200, 200);

		try {
			client = new Client(new Socket(Server.LOCALHOST, Server.DEFAULT_PORT));
			System.out.println("Uspjesna konekcija");

			client.toServer("0").thenApply(res -> {
				Platform.runLater(() -> prikazTrenutne.setText(res));
				return res;
			});

			for (int i = 0; i < Server.POCETNO; i++) {
				client.toServer("1").thenApply(res -> {
					Platform.runLater(() -> {
						listView.getItems().clear();
						Client.addRuka(res);

						for (String karta : Client.getRuka()) {
							listView.getItems().add(karta);
						}
					});

					return res;
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Button vratiKartu = new Button("povuci kartu");

		vratiKartu.setOnAction(e -> {

			client.toServer("2").thenApply(res -> {
				Platform.runLater(() -> {
					listView.getItems().clear();

					Client.addRuka(res);

					for (String karta : Client.getRuka()) {
						listView.getItems().add(karta);
					}
				});

				return res;
			});
		});

		Button odigrajKartu = new Button("odigraj kartu");

		odigrajKartu.setOnAction(e -> {
			String s = listView.getSelectionModel().getSelectedItem();

			if (s != null) {
				Client.trenutnaKarta = s;

				client.toServer("3").thenApply(res -> {
					Platform.runLater(() -> {
						prikazTrenutne.setText(res);

						Client.updateRuka(res);

						listView.getItems().clear();

						for (String karta : Client.getRuka()) {
							listView.getItems().add(karta);
						}
					});

					return res;
				});
			}
		});

		Button refresuj = new Button("refresuj tabelu");

		refresuj.setOnAction(e -> {
			client.toServer("4").thenApply(res -> {
				Platform.runLater(() -> {
					prikazTrenutne.setText(res);
				});

				return res;
			});
		});

		root.getChildren().addAll(listView, prikazTrenutne, vratiKartu, odigrajKartu, refresuj);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Klijent");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
